package com.ccandeladev.androidtesting.productlist.data.local

import android.util.Log
import com.ccandeladev.androidtesting.productlist.data.local.database.dao.OfferDao
import com.ccandeladev.androidtesting.productlist.data.local.database.dao.ProductDao
import com.ccandeladev.androidtesting.productlist.data.local.database.entity.OfferEntity
import com.ccandeladev.androidtesting.productlist.data.local.database.entity.ProductEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// For data persistence, it is used in the ProductRepositoryImpl
class LocalDataSource @Inject constructor(
    private val productDao: ProductDao,
    private val offerDao: OfferDao
) {

    fun getAllInventory(): Flow<List<ProductEntity>> = productDao.getAllInventory()

    suspend fun saveInventory(inventory: List<ProductEntity>){
        Log.e("DATABASE", "Insert data...")
        productDao.replaceAll(inventory = inventory)
    }

    suspend fun saveOffers(offers: List<OfferEntity>){
        offerDao.replaceAll(offers = offers)
    }

}