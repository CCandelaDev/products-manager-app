package com.ccandeladev.androidtesting.core.fakes

import com.ccandeladev.androidtesting.cart.domain.model.CartItem
import com.ccandeladev.androidtesting.cart.domain.repository.CartRepository
import com.ccandeladev.androidtesting.core.domain.model.AppError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

//Return The interface
class FakeCartItemRepository : CartRepository {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())

    override fun getCartItems(): Flow<List<CartItem>> {
        return _cartItems.asStateFlow()
    }

    override suspend fun addToCart(productId: String, quantity: Int) {
        val currentItems = _cartItems.value.toMutableList() //All products
        val existingIndex = currentItems.indexOfFirst { it.productId == productId }

        if (existingIndex >= 0) {
            //already exists, then we update it with the new quantity
            val item = currentItems[existingIndex]

            currentItems[existingIndex] = item.copy(quantity = item.quantity + quantity)
        } else {
            //  don`t exists, then we insert it
            currentItems.add(CartItem(productId, quantity))
        }
        _cartItems.value = currentItems
    }

    override suspend fun updateQuantity(productId: String, quantity: Int) {
        val currentItems = _cartItems.value.toMutableList() //All products
        val existingIndex = currentItems.indexOfFirst { it.productId == productId }

        if (existingIndex >= 0) {
            //already exists, add new quantity using index
            currentItems[existingIndex] =
                currentItems[existingIndex].copy(quantity = quantity)

            _cartItems.value = currentItems

        } else {
            //  don`t exists, throw error
            throw AppError.NotFoundError
        }
    }

    override suspend fun removeFromCart(productId: String) {
        val currentItems = _cartItems.value.toMutableList() //All products
        val existingIndex = currentItems.indexOfFirst { it.productId == productId }

        if (existingIndex >= 0) {
            //already exists, then delete using index
            currentItems.removeAt(existingIndex)
            _cartItems.value = currentItems  // updated

        } else {
            //  don`t exists, throw error
            throw AppError.NotFoundError
        }

    }

    override suspend fun clearCart() {
        _cartItems.value = emptyList() //Checked if it´s an empty list
    }

    override suspend fun getCartItemById(productId: String): CartItem? {
        return _cartItems.value.find {
            it.productId == productId
        }
    }
}