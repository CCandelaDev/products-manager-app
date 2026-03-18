package com.ccandeladev.androidtesting.productlist.data.repository

import com.ccandeladev.androidtesting.core.domain.coroutines.DispatchersProvider
import com.ccandeladev.androidtesting.productlist.data.remote.RemoteDataSource
import com.ccandeladev.androidtesting.productlist.domain.model.Product
import com.ccandeladev.androidtesting.productlist.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

// Product Repository Implementation
class ProductRepositoryImpl @Inject constructor(
    val remoteDataSource: RemoteDataSource,
    val dispatchers: DispatchersProvider
) :
    ProductRepository {


    override fun getInventory(): Flow<List<Product>> {
        TODO("Not yet implemented")
    }

    override fun getProductById(id: String): Flow<Product> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshProduct() {
        withContext(dispatchers.io){
            remoteDataSource.getInventory()
        }
    }
}