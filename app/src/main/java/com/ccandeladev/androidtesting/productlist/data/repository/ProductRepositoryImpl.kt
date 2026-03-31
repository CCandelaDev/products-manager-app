package com.ccandeladev.androidtesting.productlist.data.repository

import com.ccandeladev.androidtesting.core.domain.coroutines.DispatchersProvider
import com.ccandeladev.androidtesting.productlist.data.local.LocalDataSource
import com.ccandeladev.androidtesting.productlist.data.mappers.toDomain
import com.ccandeladev.androidtesting.productlist.data.mappers.toEntity
import com.ccandeladev.androidtesting.productlist.data.remote.RemoteDataSource
import com.ccandeladev.androidtesting.productlist.data.remote.response.ProductResponse
import com.ccandeladev.androidtesting.productlist.domain.model.Product
import com.ccandeladev.androidtesting.productlist.domain.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import javax.inject.Inject

// Product Repository Implementation Single source of truth
class ProductRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val dispatchers: DispatchersProvider
) :
    ProductRepository {

    private val refreshScope = CoroutineScope(context = SupervisorJob() + dispatchers.io)
    //If the user enters and exits the screen multiple times,
    // Mutex ensures only one API request at a time.
    private val refreshMutex = Mutex()



    override fun getInventory(): Flow<List<Product>> {
        return localDataSource.getAllInventory()
            .map { entities -> entities.mapNotNull { it.toDomain() } } //Filter null (no exist)
            .onStart {
                emit(emptyList()) //To initial list empty
                refreshScope.launch {
                    if (!refreshMutex.tryLock()) return@launch
                    try {
                        refreshProduct()
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

    override fun getProductById(id: String): Flow<Product?> {
        return localDataSource.getProductById(productId = id)
            .map{entity -> entity?.toDomain()}
            .catch { e ->
                //analytic.trackError (e)
            }
    }

    override fun getInventoryByIds(ids: Set<String>): Flow<List<Product>> {
        return localDataSource.getInventoryByIds(ids)
            .map { entities ->  entities.mapNotNull{ it?.toDomain() }}

    }

    override suspend fun refreshProduct() {
        withContext(dispatchers.io) {
            val inventory: List<ProductResponse> = remoteDataSource.getInventory().getOrThrow()
            val inventoryEntity = inventory.map { it.toEntity() }
            localDataSource.saveInventory(inventoryEntity)
        }
    }
}