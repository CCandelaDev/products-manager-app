package com.ccandeladev.androidtesting.cart.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ccandeladev.androidtesting.cart.domain.repository.CartRepository
import com.ccandeladev.androidtesting.cart.domain.usecase.GetCartItemsWithOffersUseCase
import com.ccandeladev.androidtesting.cart.domain.usecase.GetCartSummaryUseCase
import com.ccandeladev.androidtesting.cart.domain.usecase.UpdateCartItemUseCase
import com.ccandeladev.androidtesting.productlist.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val getCartSummaryUseCase: GetCartSummaryUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
    private val getCartItemsWithOffersUseCase: GetCartItemsWithOffersUseCase
) : ViewModel() {

    // UI state: represents what the screen should display (Loading, Success, Error)
    private val _uiState = MutableStateFlow<CartUiState>(CartUiState.Loading)
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    // One-time events: for showing toasts, navigation, etc. (not state-based)
    private val _events = MutableSharedFlow<CartEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<CartEvent> = _events

    var cartJob: Job? = null// Tracks active cart operation. Null = no operation running.
    // Used to cancel previous operations before starting new ones.

    init {
        // Load cart data as soon as ViewModel is created
        loadCart()
    }



     fun loadCart() {
        _uiState.value = CartUiState.Loading // Show loading indicator while fetching data
        cartJob?.cancel()  // Cancel any existing cart job to prevent multiple concurrent streams


        // Start new job to observe cart changes
        cartJob = combine(
            getCartItemsWithOffersUseCase(),
            getCartSummaryUseCase()
        ) { cartItemWithOffer, summary ->
            _uiState.value = CartUiState.Success(
                summary = summary,
                cartItems = cartItemWithOffer,
                isLoading = false
            )
        }.catch { e ->
            _events.emit(CartEvent.ShowMessage(e.message.orEmpty()))
        }
            .launchIn(viewModelScope)
    }



    /**
     * Updates quantity of a specific product in the cart
     * @param productId ID of the product to update
     * @param quantity New quantity (0 would remove item)
     */
    fun updateCartItem(productId: String, quantity: Int) {
        viewModelScope.launch {
            try {
                updateCartItemUseCase(productId, quantity) // Execute update
            } catch (e: Exception) {
                _events.emit(CartEvent.ShowMessage(e.message.orEmpty()))
            }
        }
    }

    /**
     * Completely removes a product from the cart
     * @param productId ID of the product to remove
     */
    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            try {
                cartRepository.removeFromCart(productId) // Execute removal
                //Show delete message
                _events.emit(CartEvent.ShowMessage("Product removed from cart"))
            } catch (e: Exception) {
                _events.emit(CartEvent.ShowMessage(e.message.orEmpty()))
            }
        }
    }

    /**
     * Increases product quantity by 1
     * Called when user clicks "+" button
     */
    fun increaseQuantity(productId: String, currentQuantity: Int) {
        updateCartItem(productId, currentQuantity + 1)
    }

    /**
     * Decreases product quantity by 1
     * If quantity becomes 0, removes the item completely
     * Called when user clicks "-" button
     */
    fun decreaseQuantity(productId: String, currentQuantity: Int) {
        if (currentQuantity > 1) {
            // Just decrease quantity
            updateCartItem(productId, currentQuantity - 1)
        } else {
            // Quantity would become 0, so remove item entirely
            removeFromCart(productId)
        }

    }


}