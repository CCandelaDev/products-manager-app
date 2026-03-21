package com.ccandeladev.androidtesting.productlist.data.repository

import com.ccandeladev.androidtesting.core.domain.coroutines.DispatchersProvider
import com.ccandeladev.androidtesting.productlist.data.local.LocalDataSource
import com.ccandeladev.androidtesting.productlist.data.local.database.entity.OfferEntity
import com.ccandeladev.androidtesting.productlist.data.mappers.toEntity
import com.ccandeladev.androidtesting.productlist.data.remote.RemoteDataSource
import com.ccandeladev.androidtesting.productlist.domain.model.Offer
import com.ccandeladev.androidtesting.productlist.domain.repository.OfferRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class OfferRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val dispatchers: DispatchersProvider,
    private val json: Json
) : OfferRepository {


    override fun getActiveOffers(): Flow<List<Offer>> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshOffers() {
        withContext(dispatchers.io) {
            val offers = remoteDataSource.getOffers().getOrThrow()
            val offersEntity: List<OfferEntity> = offers.mapNotNull {
                it.toEntity(json) //Workaround to create toDomain
            }
            localDataSource.saveOffers(offersEntity)
        }
    }
}

