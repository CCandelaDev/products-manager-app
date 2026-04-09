package com.ccandeladev.androidtesting.cart.domain.usecase

import com.ccandeladev.androidtesting.cart.domain.repository.CartRepository
import com.ccandeladev.androidtesting.core.domain.model.AppError
import com.ccandeladev.androidtesting.productlist.domain.repository.ProductRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository
) {

    suspend operator fun invoke(productId: String, quantity: Int = 1) {
        if (quantity <= 0) {
            throw AppError.Validation.QuantityMustBePositive
        }

        //val product: Flow<Product?> = productRepository.getProductById(productId)

        //To avoid having a Flow
        val product = productRepository.getProductById(productId).first()
            ?: throw AppError.NotFoundError

        val existingItem = cartRepository.getCartItemById(productId)
        val newQuantity = (existingItem?.quantity ?: 0) + quantity

        if (newQuantity > product.stock) {
            throw AppError.Validation.InsufficientStock(available = product.stock)
        }

        cartRepository.addToCart(productId, quantity)


    }
}