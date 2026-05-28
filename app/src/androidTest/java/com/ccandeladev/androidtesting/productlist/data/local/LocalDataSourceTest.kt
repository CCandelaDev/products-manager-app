package com.ccandeladev.androidtesting.productlist.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ccandeladev.androidtesting.core.builders.DEFAULT_PRODUCT_ID
import com.ccandeladev.androidtesting.core.builders.cartItemEntity
import com.ccandeladev.androidtesting.core.builders.offerEntity
import com.ccandeladev.androidtesting.core.builders.productEntity
import com.ccandeladev.androidtesting.core.data.local.database.ProductManagerDatabase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalDataSourceTest {

    private lateinit var database: ProductManagerDatabase
    private lateinit var localDataSource: LocalDataSource


    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            context = ApplicationProvider.getApplicationContext(),
            klass = ProductManagerDatabase::class.java
        ).build()

        localDataSource =
            LocalDataSource(
                database.productDao(),
                database.offerDao(),
                database.cartItemDao()
            )
    }

    @After
    fun tearDown() {
        database.close()
    }

    //Products
    @Test
    fun givenProducts_whenSaveAndGetAll_thenReturnsPersistedProduct() = runTest {

        val products = listOf(
            productEntity { withId("1") },
            productEntity { withId("2") }
        )

        localDataSource.saveInventory(products)

        val result = localDataSource.getAllInventory().first()

        assertTrue(result.size == 2)
        assertEquals(products, result)
    }

    @Test
    fun givenSavedProducts_whenGetProductById_thenReturnsCorrectProduct() = runTest {
        val products = listOf(
            productEntity { withId("1"); withName("clothes") },
            productEntity { withId("2"); withName("shoes") },
            productEntity { withId("3"); withName("weights") }
        )

        localDataSource.saveInventory(products)
        val result = localDataSource.getInventoryByIds(setOf("1", "3")).first()

        assertTrue(result.size == 2)
        assertTrue(result.any() { it?.id == "clothes" })
        assertTrue(result.any() { it?.id == "weights" })
    }

    //Offers
    @Test
    fun givenOffers_whenSaveAndGetAll_thenReturnsPersistedPromotion() = runTest {

        val offers = listOf(
            offerEntity { withId("id1") },
            offerEntity { withId("id2"); withProductIds("""["p-id1"]""") }
        )

        localDataSource.saveOffers(offers)

        val result = localDataSource.getAllOffers().first()

        assertTrue(result.size == 2)
        assertEquals(offers, result)
    }

    @Test
    fun givenCartItem_whenInsertCartItem_thenReturnsSuccessItemSaved() = runTest {

        val cartItem = cartItemEntity { withProductId(DEFAULT_PRODUCT_ID); withQuantity(3) }

        val result = localDataSource.insertCartItem(cartItem)
        assertTrue(result.isSuccess)

        val items = localDataSource.getAllCartItems().first()

        assertTrue(items.size == 1)
        assertEquals(DEFAULT_PRODUCT_ID, items.first().productId)
        assertEquals(3, items.first().quantity)
    }

    @Test
    fun givenExistingCartItem_whenUpdateCartItem_thenReturnsSuccessAndCartItemUpdate() = runTest {

        val cartItem = cartItemEntity { withProductId(DEFAULT_PRODUCT_ID); withQuantity(3) }
        localDataSource.insertCartItem(cartItem)

        val updatedCartItem = cartItem.copy(quantity = 5)
        val result = localDataSource.updateCartItem(updatedCartItem)
        assertTrue(result.isSuccess)

        val item = localDataSource.getCartItemById(DEFAULT_PRODUCT_ID)
        assertNotNull(item)
        assertEquals(5, item?.quantity)

    }

    @Test
    fun givenSavedCartItem_whenDeleteCartItem_thenItemIsRemoved() = runTest {
        val cartItem = cartItemEntity { withProductId(DEFAULT_PRODUCT_ID) }
        localDataSource.insertCartItem(cartItem)

        val result = localDataSource.deleteCartItem(cartItem)
        assertTrue(result.isSuccess)

        val items = localDataSource.getAllCartItems().first()
        assertTrue(items.isEmpty())
    }

    @Test
    fun givenSavedCartItems_whenCleanCartItem_thenCartIsEmpty() = runTest {
        val cartItem1 = cartItemEntity { withProductId("id1") }
        localDataSource.insertCartItem(cartItem1)
        val cartItem2 = cartItemEntity { withProductId("id2") }
        localDataSource.insertCartItem(cartItem2)

        val result = localDataSource.clearCart()
        assertTrue(result.isSuccess)

        val items = localDataSource.getAllCartItems().first()

        assertTrue(items.isEmpty())


    }

}