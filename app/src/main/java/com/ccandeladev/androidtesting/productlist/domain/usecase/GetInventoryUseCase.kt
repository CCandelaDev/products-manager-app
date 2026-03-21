package com.ccandeladev.androidtesting.productlist.domain.usecase

import com.ccandeladev.androidtesting.productlist.domain.model.Product
import com.ccandeladev.androidtesting.productlist.domain.repository.OfferRepository
import com.ccandeladev.androidtesting.productlist.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetInventoryUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val offerRepository: OfferRepository
) {

    operator fun invoke(): Flow<List<Product>>{
        //combine product and offer
         productRepository.getInventory()
         offerRepository.getActiveOffers()
    }
}