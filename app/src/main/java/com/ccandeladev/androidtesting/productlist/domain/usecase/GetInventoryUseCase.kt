package com.ccandeladev.androidtesting.productlist.domain.usecase

import com.ccandeladev.androidtesting.core.domain.util.Clock
import com.ccandeladev.androidtesting.core.presentation.ex.activeAt
import com.ccandeladev.androidtesting.productlist.domain.model.ProductWithOffer
import com.ccandeladev.androidtesting.productlist.domain.repository.OfferRepository
import com.ccandeladev.androidtesting.productlist.domain.repository.ProductRepository
import com.ccandeladev.androidtesting.productlist.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetInventoryUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val offerRepository: OfferRepository,
    private val getOfferForProduct: GetOfferForProduct,
    private val settingsRepository: SettingsRepository,
    private val clock: Clock
) {

    operator fun invoke(): Flow<List<ProductWithOffer>> {
        //combine product and offer
        return combine(
            flow = productRepository.getInventory(),
            flow2 = offerRepository.getActiveOffers(),
            flow3 = settingsRepository.inStockOnly
        ) { inventory, offers, inStockOnly ->

            //val now = Instant.now()
            val now = clock.now()

            val activeOffers = offers.activeAt(now = now) // Extension function


            val filteredInventory = if (inStockOnly) {
                inventory.filter { it.stock > 0 }
            } else {
                inventory
            }

            filteredInventory.map { product ->
                val offer = getOfferForProduct(product, activeOffers)
                ProductWithOffer(product = product, offer = offer)

            }
        }
    }
}