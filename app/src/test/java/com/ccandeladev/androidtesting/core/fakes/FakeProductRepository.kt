package com.ccandeladev.androidtesting.core.fakes

import com.ccandeladev.androidtesting.productlist.domain.model.Product
import com.ccandeladev.androidtesting.productlist.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeProductRepository: ProductRepository {

    //Initial state is empty
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    //We don't want to access  external database

    override fun getInventory(): Flow<List<Product>> {
        return _products.asStateFlow()
    }

    override fun getProductById(id: String): Flow<Product?> {
        //to find product with that id
        return _products.asStateFlow().map { products ->
            products.find { it.id == id }
        }
    }

    override fun getInventoryByIds(ids: Set<String>): Flow<List<Product>> {
        return _products.asStateFlow().map { products ->
            products.filter { it.id in ids }

        }
    }

    override suspend fun refreshProduct() {
        //No effect
    }


}