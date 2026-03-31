package com.ccandeladev.androidtesting.cart.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ccandeladev.androidtesting.cart.domain.repository.CartRepository
import com.ccandeladev.androidtesting.cart.domain.usecase.GetCartSummaryUseCase
import com.ccandeladev.androidtesting.cart.domain.usecase.UpdateCartItemUseCase
import com.ccandeladev.androidtesting.cart.presentation.model.CartItemWithOffer
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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val getCartSummaryUseCase: GetCartSummaryUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase
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


    /**
     * Loads cart data and observes real-time changes
     * This function sets up a reactive stream that:
     * 1. Listens to cart item changes
     * 2. Fetches product details for items in cart
     * 3. Gets cart summary with applied discounts
     * 4. Combines everything into UI state
     */
    fun loadCart() {
        _uiState.value = CartUiState.Loading // Show loading indicator while fetching data
        cartJob?.cancel()  // Cancel any existing cart job to prevent multiple concurrent streams

        // Start new job to observe cart changes
        cartJob =
            cartRepository.getCartItems()
                // flatMapLatest: it ensures that only the last transaction is processed.
                .flatMapLatest { cartItems ->
                    // Extract unique product IDs from cart items
                    val ids = cartItems.mapTo(mutableSetOf()) { it.productId }

                    // Handle empty cart scenario
                    if (ids.isEmpty()) {
                        // Cart is empty: just get summary (which will be zero) and emit success state
                        getCartSummaryUseCase().map { summary ->
                            _uiState.value = CartUiState.Success(
                                summary = summary,
                                cartItems = emptyList(),
                                isLoading = false
                            )

                        }

                    } else {
                        // Cart has items: combine product details with cart summary
                        combine(
                            // Source 1: Get product details (prices, names, images) for all products in cart
                            productRepository.getInventoryByIds(ids),
                            // Source 2: Get cart summary with discounts applied
                            getCartSummaryUseCase()
                        ) { products, summary ->

                            // Create a map for O(1) product lookup by ID

                            val productsById = products.associateBy { it.id }
                            // Enrich each cart item with its full product details

                            val cartItemsWithProducts = cartItems.mapNotNull { cartItem ->

                                val finalProduct =
                                    productsById[cartItem.productId]
                                        ?: return@mapNotNull null // Skip if product not found

                                CartItemWithOffer(
                                    product = finalProduct,
                                    cartItem = cartItem
                                )

                            }
                            // Update UI state with cart items and summary
                            _uiState.value = CartUiState.Success(
                                isLoading = false,
                                cartItems = cartItemsWithProducts,
                                summary = summary
                            )

                        }
                    }


                } // Handle any errors that occur during the stream
                .catch { e ->
                    _uiState.value = CartUiState.Error(e.message.orEmpty())

                } // Launch the coroutine in ViewModel's scope (automatically cancels on ViewModel destruction)
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