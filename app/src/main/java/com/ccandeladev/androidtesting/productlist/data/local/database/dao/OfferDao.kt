package com.ccandeladev.androidtesting.productlist.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ccandeladev.androidtesting.productlist.data.local.database.entity.OfferEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OfferDao {

    @Query("SELECT * FROM offers")
    fun getAllOffers(): Flow<List<OfferEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffers(offers: List<OfferEntity>)

    @Query("DELETE FROM offers")
    suspend fun clearOffers()

    @Transaction
    suspend fun replaceAll(offers: List<OfferEntity>){
        clearOffers()
        insertOffers(offers = offers)
    }

}