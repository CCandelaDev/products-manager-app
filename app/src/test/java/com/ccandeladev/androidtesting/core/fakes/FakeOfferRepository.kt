package com.ccandeladev.androidtesting.core.fakes

import com.ccandeladev.androidtesting.productlist.domain.model.Offer
import com.ccandeladev.androidtesting.productlist.domain.repository.OfferRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeOfferRepository : OfferRepository {

    // Fake list initialize with empty list
    private val _offers = MutableStateFlow<List<Offer>>(emptyList())

    // To set offers at startup
    fun setOffers(offers: List<Offer>){
        _offers.value = offers
    }

    override fun getActiveOffers(): Flow<List<Offer>> {
        return _offers.asStateFlow()
    }

    // Not necessary
    override suspend fun refreshOffers() {}
}