package com.ccandeladev.androidtesting.cart.data.repository

import com.ccandeladev.androidtesting.cart.data.mapper.toDomain
import com.ccandeladev.androidtesting.cart.data.mapper.toEntity
import com.ccandeladev.androidtesting.cart.domain.model.CartItem
import com.ccandeladev.androidtesting.cart.domain.repository.CartRepository
import com.ccandeladev.androidtesting.core.domain.model.AppError
import com.ccandeladev.androidtesting.productlist.data.local.LocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// The business will be managed in the use cases
class CartRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource
) : CartRepository {
    override fun getCartItems(): Flow<List<CartItem>> {
        return localDataSource.getAllCartItems()
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun addToCart(productId: String, quantity: Int) {
        // To manage if product exist in the cart
       val existingItem = localDataSource.getCartItemById(productId = productId)
        if (existingItem != null){
            val newQuantity = existingItem.quantity + quantity
            localDataSource.updateCartItem(existingItem.copy(quantity = newQuantity))
        }else{
            localDataSource.insertCartItem(CartItem(productId, quantity).toEntity())
        }
    }

    override suspend fun updateQuantity(productId: String, quantity: Int) {
        val item = localDataSource.getCartItemById(productId = productId) ?: throw AppError.NotFoundError
        localDataSource.updateCartItem(item.copy(quantity = quantity))
    }

    override suspend fun removeFromCart(productId: String) {
        val item = localDataSource.getCartItemById(productId) ?: throw AppError.NotFoundError
        localDataSource.deleteCartItem(item)
    }

    override suspend fun clearCart() {
        localDataSource.clearCart()
    }
}