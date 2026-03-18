package com.ccandeladev.androidtesting.productlist.data.repository

import com.ccandeladev.androidtesting.productlist.data.remote.RemoteDataSource
import com.ccandeladev.androidtesting.productlist.domain.model.Product
import com.ccandeladev.androidtesting.productlist.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Product Repository Implementation
class ProductRepositoryImpl @Inject constructor(remoteDataSource: RemoteDataSource): ProductRepository {
    override fun getInventory(): Flow<List<Product>> {
        TODO("Not yet implemented")
    }

    override fun getProductById(id: String): Flow<Product> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshProduct() {
        TODO("Not yet implemented")
    }
}