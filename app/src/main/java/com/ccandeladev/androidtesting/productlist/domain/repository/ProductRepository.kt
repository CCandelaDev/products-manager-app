package com.ccandeladev.androidtesting.productlist.domain.repository

import com.ccandeladev.androidtesting.productlist.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getInventory(): Flow<List<Product>>
    fun getProductById(id: String): Flow<Product?>
    suspend fun refreshProduct()
}