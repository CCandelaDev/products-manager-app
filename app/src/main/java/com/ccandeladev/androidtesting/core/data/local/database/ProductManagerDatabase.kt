package com.ccandeladev.androidtesting.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ccandeladev.androidtesting.cart.data.local.database.dao.CartDao
import com.ccandeladev.androidtesting.cart.data.local.database.entity.CartEntity
import com.ccandeladev.androidtesting.productlist.data.local.database.dao.OfferDao
import com.ccandeladev.androidtesting.productlist.data.local.database.dao.ProductDao
import com.ccandeladev.androidtesting.productlist.data.local.database.entity.OfferEntity
import com.ccandeladev.androidtesting.productlist.data.local.database.entity.ProductEntity

//Methods for providing DAO (Data access object)
@Database(
    entities = [ProductEntity::class, OfferEntity::class, CartEntity::class],
    version = 2,
    exportSchema = true
)
abstract class ProductManagerDatabase: RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun offerDao(): OfferDao

    abstract fun cartItemDao(): CartDao
}