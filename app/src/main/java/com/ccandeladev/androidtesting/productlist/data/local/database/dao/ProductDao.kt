package com.ccandeladev.androidtesting.productlist.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ccandeladev.androidtesting.productlist.data.local.database.entity.ProductEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface ProductDao {
    @Query(value = "SELECT * FROM inventory")
    fun getAllInventory(): Flow<List<ProductEntity>>

    @Query(value = "SELECT * FROM inventory WHERE id=:id")
    fun getProductById(id: String): Flow<ProductEntity>

    //Only for insert, not return
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventory(inventory: List<ProductEntity>)

    @Query("DELETE FROM inventory")
    suspend fun clearInventory()

    // Both must be fulfilled or rollback
    @Transaction
    suspend fun replaceAll(inventory:List<ProductEntity>){
        clearInventory()
        insertInventory(inventory = inventory)
    }

}