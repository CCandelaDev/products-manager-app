package com.ccandeladev.androidtesting.productlist.domain.repository

import com.ccandeladev.androidtesting.productlist.domain.model.Offer
import kotlinx.coroutines.flow.Flow

interface OfferRepository {

    fun getActiveOffers(): Flow<List<Offer>>

    suspend fun refreshOffers()
}