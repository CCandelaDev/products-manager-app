package com.ccandeladev.androidtesting.productlist.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

//To map the data that is in the data class product
@Entity(tableName = "inventory")
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String?,
    val price: Double,
    val category: String?,
    val stock: Int?,
    val imageUrl: String? = null
)
