package com.ccandeladev.androidtesting.detail.domain.usecase

import com.ccandeladev.androidtesting.core.domain.util.Clock
import com.ccandeladev.androidtesting.core.presentation.ex.activeAt
import com.ccandeladev.androidtesting.productlist.domain.model.ProductWithOffer
import com.ccandeladev.androidtesting.productlist.domain.repository.OfferRepository
import com.ccandeladev.androidtesting.productlist.domain.repository.ProductRepository
import com.ccandeladev.androidtesting.productlist.domain.usecase.GetOfferForProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetProductDetailWithOfferUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val offerRepository: OfferRepository,
    private val getOfferForProduct: GetOfferForProduct,
    private val clock: Clock
) {
    operator fun invoke(productId: String): Flow<ProductWithOffer?> {
        return combine(
            // Observes both flows simultaneously and re-executes the block
            // whenever either of them emits a new value
            productRepository.getProductById(productId),
            offerRepository.getActiveOffers()
        ) { product, offers ->
            val now = clock.now()
            val activeOffers = offers.activeAt(now = now)
            // Typically used with ?.let { } for null safety
            product?.let {
                val finalOffer = getOfferForProduct(it, activeOffers)
                ProductWithOffer(product = it, offer = finalOffer)
            }

        }

    }
}