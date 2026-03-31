package com.ccandeladev.androidtesting.productlist.domain.repository

import com.ccandeladev.androidtesting.productlist.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getInventory(): Flow<List<Product>>
    fun getProductById(id: String): Flow<Product?>
    //With set, we prevent the same value from being entered twice
    fun getInventoryByIds(ids: Set<String>): Flow<List<Product>>
    suspend fun refreshProduct()
}