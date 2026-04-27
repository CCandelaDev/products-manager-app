package com.ccandeladev.androidtesting.cart.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ccandeladev.androidtesting.cart.domain.repository.CartRepository
import com.ccandeladev.androidtesting.cart.domain.usecase.GetCartItemsWithOffersUseCase
import com.ccandeladev.androidtesting.cart.domain.usecase.GetCartSummaryUseCase
import com.ccandeladev.androidtesting.cart.domain.usecase.UpdateCartItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    getCartSummaryUseCase: GetCartSummaryUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
    getCartItemsWithOffersUseCase: GetCartItemsWithOffersUseCase
) : ViewModel() {

    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    // UI state: represents what the screen should display (Loading, Success, Error)
    val uiState: StateFlow<CartUiState> = combine(
        refreshTrigger.onStart { emit(Unit) },
        getCartItemsWithOffersUseCase(),
        getCartSummaryUseCase()

    ) { _, cartItemWithOffer, summary ->
        CartUiState.Success(
            summary = summary,
            cartItems = cartItemWithOffer,
            isLoading = false
        ) as CartUiState
    }.catch { e ->
        _events.emit(CartEvent.ShowMessage(e.message.orEmpty()))
        emit(CartUiState.Error(e.message.orEmpty()))

    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CartUiState.Loading
        )

    // One-time events: for showing toasts, navigation, etc. (not state-based)
    private val _events = MutableSharedFlow<CartEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<CartEvent> = _events


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

    fun refresh(){
        refreshTrigger.tryEmit(Unit) //it does nothing, relaunch the success
    }


}