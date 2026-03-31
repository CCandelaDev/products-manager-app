package com.ccandeladev.androidtesting.cart.presentation

import com.ccandeladev.androidtesting.cart.domain.model.CartSummary
import com.ccandeladev.androidtesting.cart.presentation.model.CartItemWithOffer

sealed class CartUiState {
    data class Success(
        val summary: CartSummary? = null,
        val cartItems: List<CartItemWithOffer>,
        val isLoading: Boolean
    ) : CartUiState()

    data class Error(val message: String) : CartUiState()

    data object Loading : CartUiState()
}


