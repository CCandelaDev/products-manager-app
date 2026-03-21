package com.ccandeladev.androidtesting.productlist.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offers")
data class OfferEntity(
    @PrimaryKey
    val id: String,
    val productIds: String,
    val type: String,
    val percent: Int? = null,
    val buyX: Int? = null,
    val payY: Int? = null,
    val startAtEpoch: Long? = null,
    val endAtEpoch: Long? = null,

    )
