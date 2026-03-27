package com.ccandeladev.androidtesting.productlist.data.local

import android.util.Log
import com.ccandeladev.androidtesting.cart.data.local.database.dao.CartItemDao
import com.ccandeladev.androidtesting.cart.data.local.database.entity.CartItemEntity
import com.ccandeladev.androidtesting.productlist.data.local.database.dao.OfferDao
import com.ccandeladev.androidtesting.productlist.data.local.database.dao.ProductDao
import com.ccandeladev.androidtesting.productlist.data.local.database.entity.OfferEntity
import com.ccandeladev.androidtesting.productlist.data.local.database.entity.ProductEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// For data persistence, it is used in the ProductRepositoryImpl
class LocalDataSource @Inject constructor(
    private val productDao: ProductDao,
    private val offerDao: OfferDao,
    private val cartItemDao: CartItemDao
) {

    fun getAllInventory(): Flow<List<ProductEntity>> = productDao.getAllInventory()

    fun getProductById(productId: String): Flow<ProductEntity?> =
        productDao.getProductById(productId)

    fun getAllOffers(): Flow<List<OfferEntity>> = offerDao.getAllOffers()

    suspend fun saveInventory(inventory: List<ProductEntity>) {
        Log.e("DATABASE", "Insert data...")
        productDao.replaceAll(inventory = inventory)
    }

    suspend fun saveOffers(offers: List<OfferEntity>) {
        offerDao.replaceAll(offers = offers)
    }

    fun getAllCartItems(): Flow<List<CartItemEntity>> = cartItemDao.getAllCartItems()

    suspend fun getCartItemById(productId: String): CartItemEntity? =
        cartItemDao.getCartItemById(productId)

    suspend fun updateCartItem(cartItemEntity: CartItemEntity): Result<Unit> {
        return try {
            cartItemDao.updateCartItem(cartItemEntity)
            Result.success(Unit)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun insertCartItem(cartItemEntity: CartItemEntity): Result<Unit>{
        return runCatching { //workaround
            cartItemDao.insertCartItem(cartItem = cartItemEntity)
        }
    }
    suspend fun deleteCartItem(cartItemEntity: CartItemEntity): Result<Unit>{
        return runCatching { //workaround
            cartItemDao.deleteCartItem(cartItem = cartItemEntity)
        }
    }

    suspend fun clearCart(): Result<Unit>{
        return runCatching { //workaround
            cartItemDao.clearCart()
        }
    }


}