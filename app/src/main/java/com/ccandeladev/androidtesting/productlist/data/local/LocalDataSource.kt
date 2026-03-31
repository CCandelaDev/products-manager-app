package com.ccandeladev.androidtesting.productlist.data.local

import android.util.Log
import com.ccandeladev.androidtesting.cart.data.local.database.dao.CartDao
import com.ccandeladev.androidtesting.cart.data.local.database.entity.CartEntity
import com.ccandeladev.androidtesting.productlist.data.local.database.dao.OfferDao
import com.ccandeladev.androidtesting.productlist.data.local.database.dao.ProductDao
import com.ccandeladev.androidtesting.productlist.data.local.database.entity.OfferEntity
import com.ccandeladev.androidtesting.productlist.data.local.database.entity.ProductEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

// For data persistence, it is used in the ProductRepositoryImpl and CartRepositoryImpl
class LocalDataSource @Inject constructor(
    private val productDao: ProductDao,
    private val offerDao: OfferDao,
    private val cartDao: CartDao
) {

    fun getAllInventory(): Flow<List<ProductEntity>> = productDao.getAllInventory()

    fun getProductById(productId: String): Flow<ProductEntity?> =
        productDao.getProductById(productId)

    fun getInventoryByIds(productIds: Set<String>): Flow<List<ProductEntity?>>{
        if(productIds.isEmpty()) return flowOf(emptyList())

        return productDao.getInventoryByIds(productIds.toList()) //No support Set
    }

    fun getAllOffers(): Flow<List<OfferEntity>> = offerDao.getAllOffers()

    suspend fun saveInventory(inventory: List<ProductEntity>) {
        Log.e("DATABASE", "Insert data...")
        productDao.replaceAll(inventory = inventory)
    }

    suspend fun saveOffers(offers: List<OfferEntity>) {
        offerDao.replaceAll(offers = offers)
    }

    fun getAllCartItems(): Flow<List<CartEntity>> = cartDao.getAllCartItems()

    suspend fun getCartItemById(productId: String): CartEntity? =
        cartDao.getCartItemById(productId)

    suspend fun updateCartItem(cartEntity: CartEntity): Result<Unit> {
        return try {
            cartDao.updateCartItem(cartEntity)
            Result.success(Unit)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun insertCartItem(cartEntity: CartEntity): Result<Unit>{
        return runCatching { //workaround
            cartDao.insertCartItem(cartItem = cartEntity)
        }
    }
    suspend fun deleteCartItem(cartEntity: CartEntity): Result<Unit>{
        return runCatching { //workaround
            cartDao.deleteCartItem(cartItem = cartEntity)
        }
    }

    suspend fun clearCart(): Result<Unit>{
        return runCatching { //workaround
            cartDao.clearCart()
        }
    }


}