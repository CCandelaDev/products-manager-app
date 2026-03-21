package com.ccandeladev.androidtesting.productlist.data.repository

import com.ccandeladev.androidtesting.core.domain.coroutines.DispatchersProvider
import com.ccandeladev.androidtesting.productlist.data.local.LocalDataSource
import com.ccandeladev.androidtesting.productlist.data.local.database.entity.OfferEntity
import com.ccandeladev.androidtesting.productlist.data.mappers.toDomain
import com.ccandeladev.androidtesting.productlist.data.mappers.toEntity
import com.ccandeladev.androidtesting.productlist.data.remote.RemoteDataSource
import com.ccandeladev.androidtesting.productlist.domain.model.Offer
import com.ccandeladev.androidtesting.productlist.domain.repository.OfferRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class OfferRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val dispatchers: DispatchersProvider,
    private val json: Json
) : OfferRepository {

    private val refreshScope = CoroutineScope(context = SupervisorJob() + dispatchers.io)
    //If the user enters and exits the screen multiple times,
    // Mutex ensures only one API request at a time.
    private val refreshMutex = Mutex()


    override fun getActiveOffers(): Flow<List<Offer>> {
        return localDataSource.getAllOffers()
            .map { entities -> entities.mapNotNull{ it.toDomain(json) } } //Filter null (no exist)
            .onStart {
                refreshScope.launch {
                    if (!refreshMutex.tryLock()) return@launch
                    try {
                        refreshOffers()
                    } catch (e: Exception) {

                    } finally {
                        refreshMutex.unlock()
                    }

                }

            }
            .catch {
                //Important Log to analyze
            }
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

