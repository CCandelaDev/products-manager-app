package com.ccandeladev.androidtesting.cart.domain.repository

import com.ccandeladev.androidtesting.cart.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    // CartItem is created in model
    fun getCartItems(): Flow<List<CartItem>>

    suspend fun addToCart(productId: String, quantity: Int)

    suspend fun updateQuantity(productId: String, quantity: Int)

    suspend fun removeFromCart(productId: String)

    suspend fun clearCart()
}