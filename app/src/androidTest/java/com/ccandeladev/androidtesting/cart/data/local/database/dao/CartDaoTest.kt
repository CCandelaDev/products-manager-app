package com.ccandeladev.androidtesting.cart.data.local.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ccandeladev.androidtesting.core.builders.DEFAULT_PRODUCT_ID
import com.ccandeladev.androidtesting.core.builders.cartItemEntity
import com.ccandeladev.androidtesting.core.data.local.database.ProductManagerDatabase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CartDaoTest {

    private lateinit var database: ProductManagerDatabase
    private lateinit var dao: CartDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            context = ApplicationProvider.getApplicationContext(),
            klass = ProductManagerDatabase::class.java
        ).build()

        dao = database.cartItemDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun given_emptyCart_when_getAllCartItems_then_EmitsEmptyList () = runTest {

        val cart = dao.getAllCartItems().first()

        assertTrue(cart.isEmpty())
    }

    @Test
    fun givenEmptyCart_whenInsertItem_thenItemIsPersisted () = runTest {

        dao.insertCartItem(cartItemEntity { withProductId(DEFAULT_PRODUCT_ID); withQuantity(2) })

        val itemCart = dao.getAllCartItems().first()

        assertEquals(1, itemCart.size)
        assertEquals(DEFAULT_PRODUCT_ID, itemCart.first().productId)
        assertEquals(2, itemCart.first().quantity)

    }

    @Test
    fun givenInsertItem_whenGetItemById_thenCurrentItem () = runTest {

        dao.insertCartItem(cartItemEntity { withProductId(DEFAULT_PRODUCT_ID); withQuantity(2) })

        val itemCart = dao.getCartItemById(DEFAULT_PRODUCT_ID)

        assertEquals(DEFAULT_PRODUCT_ID, itemCart?.productId)
        assertEquals(2, itemCart?.quantity)

    }

    @Test
    fun givenEmptyCart_whenGetItemById_thenReturnNull () = runTest {

        val itemCart = dao.getCartItemById(DEFAULT_PRODUCT_ID)

        assertEquals(null, itemCart?.productId)
        assertNull(itemCart)

    }

    @Test
    fun givenExistingItem_whenUpdateItemQuantity_thenQuantityIsUpdated () = runTest {

        val item = cartItemEntity { withProductId(DEFAULT_PRODUCT_ID); withQuantity(3) }
        dao.insertCartItem(item)

        val itemUpdate = cartItemEntity { withProductId(DEFAULT_PRODUCT_ID); withQuantity(4) }
        dao.insertCartItem(itemUpdate)

        val itemCart = dao.getCartItemById(DEFAULT_PRODUCT_ID)

        assertEquals(DEFAULT_PRODUCT_ID, itemCart?.productId)
        assertEquals(4, itemCart?.quantity)
    }

    @Test
    fun givenItemCart_whenDeleteItem_thenCartIsEmptyEmpty () = runTest {

        val item = cartItemEntity { withProductId(DEFAULT_PRODUCT_ID); withQuantity(3) }
        dao.insertCartItem(item)
        dao.deleteCartItem(item)

        val itemCart = dao.getCartItemById(DEFAULT_PRODUCT_ID)
        assertNull(itemCart)

        val allItems = dao.getAllCartItems().first()
        assertTrue(allItems.isEmpty())
    }

    @Test
    fun givenMultipleItem_whenClearCart_thenAllItemsAreRemoved () = runTest {
        val itemOne = cartItemEntity { withProductId("1"); withQuantity(3) }
        dao.insertCartItem(itemOne)
        val itemTwo = cartItemEntity { withProductId("2"); withQuantity(5) }
        dao.insertCartItem(itemTwo)

        dao.clearCart()

        val itemsCart = dao.getAllCartItems().first()
        assertTrue(itemsCart.isEmpty())

    }

    @Test
    fun givenExistingItemId_whenInsertDuplicateId_thenItemIsReplaced () = runTest {
        val item = cartItemEntity { withProductId(DEFAULT_PRODUCT_ID); withQuantity(3) }
        dao.insertCartItem(item)

        val newItem = cartItemEntity { withProductId(DEFAULT_PRODUCT_ID); withQuantity(5) }
        dao.insertCartItem(newItem)

        val itemCart = dao.getCartItemById(DEFAULT_PRODUCT_ID)

        assertEquals(DEFAULT_PRODUCT_ID, itemCart?.productId)
        assertEquals(5, itemCart?.quantity)

    }
}