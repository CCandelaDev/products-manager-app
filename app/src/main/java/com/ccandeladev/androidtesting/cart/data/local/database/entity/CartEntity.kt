package com.ccandeladev.androidtesting.cart.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartEntity (
    @PrimaryKey
    val productId: String,
    val quantity: Int
)
