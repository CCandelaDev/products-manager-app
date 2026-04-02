package com.ccandeladev.androidtesting.cart.domain.usecase

import com.ccandeladev.androidtesting.cart.domain.ex.activeAt
import com.ccandeladev.androidtesting.cart.domain.repository.CartRepository
import com.ccandeladev.androidtesting.cart.presentation.model.CartItemWithOffer
import com.ccandeladev.androidtesting.productlist.domain.model.ProductWithOffer
import com.ccandeladev.androidtesting.productlist.domain.repository.OfferRepository
import com.ccandeladev.androidtesting.productlist.domain.repository.ProductRepository
import com.ccandeladev.androidtesting.productlist.domain.usecase.GetOfferForProduct
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.time.Instant
import javax.inject.Inject

class GetCartItemsWithOffersUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val offerRepository: OfferRepository,
    private val getOfferForProduct: GetOfferForProduct
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<CartItemWithOffer>> {

        return cartRepository.getCartItems().flatMapLatest { cartItems ->
            val ids = cartItems.mapTo(mutableSetOf()) { it.productId }

            if (ids.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(
                    productRepository.getInventoryByIds(ids),
                    offerRepository.getActiveOffers()
                ) { products, offers ->
                    val now = Instant.now()
                    val activeOffers = offers.activeAt(now)
                    val productsById = products.associateBy { it.id }
                    cartItems.mapNotNull { cartItem ->
                        val product = productsById[cartItem.productId] ?: return@mapNotNull null
                        val offer = getOfferForProduct(product = product, offers = activeOffers)
                        val productWithOffer = ProductWithOffer(product = product, offer = offer)
                        CartItemWithOffer(cartItem = cartItem, item =productWithOffer)
                    }


                }
            }
        }
    }
}