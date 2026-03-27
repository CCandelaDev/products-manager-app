package com.ccandeladev.androidtesting.cart.data.repository

import com.ccandeladev.androidtesting.cart.domain.model.CartItem
import com.ccandeladev.androidtesting.cart.domain.repository.CartItemRepository
import kotlinx.coroutines.flow.Flow

class CartItemRepositoryImpl: CartItemRepository {
    override fun getCartItems(): Flow<List<CartItem>> {
        TODO("Not yet implemented")
    }

    override suspend fun addToCart(productId: String, quantity: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun updateQuantity(productId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun removeFromCart(productId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun clearCart() {
        TODO("Not yet implemented")
    }
}