package com.ccandeladev.androidtesting.core.stubs

import com.ccandeladev.androidtesting.productlist.domain.model.Product
import com.ccandeladev.androidtesting.productlist.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FailingProductRepositoryStub(private val exception: Throwable): ProductRepository {
    override fun getInventory(): Flow<List<Product>> = flow { throw exception }

    override fun getProductById(id: String): Flow<Product?> = flowOf()

    override fun getInventoryByIds(ids: Set<String>): Flow<List<Product>> = flowOf()

    override suspend fun refreshProduct() {}
}