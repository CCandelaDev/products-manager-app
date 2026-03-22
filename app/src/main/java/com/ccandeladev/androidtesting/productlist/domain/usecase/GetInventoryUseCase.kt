package com.ccandeladev.androidtesting.productlist.domain.usecase

import com.ccandeladev.androidtesting.productlist.domain.model.ProductWithOffer
import com.ccandeladev.androidtesting.productlist.domain.repository.OfferRepository
import com.ccandeladev.androidtesting.productlist.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Instant
import javax.inject.Inject

class GetInventoryUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val offerRepository: OfferRepository,
    private val getOfferForProduct: GetOfferForProduct
) {

    operator fun invoke(): Flow<List<ProductWithOffer>> {
        //combine product and offer
        return combine(
            flow = productRepository.getInventory(),
            flow2 = offerRepository.getActiveOffers()
        ) { inventory, offers ->

            val now = Instant.now()

            val activeOffers = offers.filter {
                it.startTime <= now && it.endTime >= now
            }

            inventory.map { product ->
                val offer = getOfferForProduct(product, activeOffers)
                ProductWithOffer(product = product, offer = offer)

            }
        }
    }
}