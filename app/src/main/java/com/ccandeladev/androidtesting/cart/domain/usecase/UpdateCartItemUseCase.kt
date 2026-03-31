package com.ccandeladev.androidtesting.cart.domain.usecase

import com.ccandeladev.androidtesting.cart.domain.repository.CartRepository
import com.ccandeladev.androidtesting.core.domain.model.AppError
import com.ccandeladev.androidtesting.productlist.domain.repository.ProductRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UpdateCartItemUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(productId: String, quantity: Int) {

        if (quantity < 0) {
            throw AppError.Validation.QuantityMustBePositive
        }

        if (quantity == 0) {
            cartRepository.removeFromCart(productId)
        }

        val product = productRepository.getProductById(productId).first() ?: throw AppError.NotFoundError

        if (quantity > product.stock){
            throw AppError.Validation.InsufficientStock(available = product.stock)
        }

        cartRepository.updateQuantity(productId, quantity)
    }
}