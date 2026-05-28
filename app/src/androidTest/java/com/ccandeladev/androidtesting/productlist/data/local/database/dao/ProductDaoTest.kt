package com.ccandeladev.androidtesting.productlist.data.local.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.ccandeladev.androidtesting.core.builders.DEFAULT_PRODUCT_ID
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

@RunWith(AndroidJUnit4::class)  //Android context
class ProductDaoTest {

    private lateinit var database: ProductManagerDatabase
    private lateinit var dao: ProductDao

    @Before
    fun setUp() { // To create one time use DB
        database = Room.inMemoryDatabaseBuilder(
            context = ApplicationProvider.getApplicationContext(),
            klass = ProductManagerDatabase::class.java
        ).build()

        dao = database.productDao() //dao instance
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun given_emptyDatabase_when_getInventoryByIds_then_emitsEmptyList() = runTest {
        //Given: empty database. setup

        //When
        val inventory = dao.getAllInventory().first()

        //Then
        assertTrue(inventory.isEmpty())
    }


    @Test
    fun given_product_when_getProductById_then_returnsRow() = runTest {
        //Given
        val p = productEntity { withId(DEFAULT_PRODUCT_ID) }
        dao.insertInventory(listOf(p))

        //When
        val product = dao.getProductById(DEFAULT_PRODUCT_ID).first()

        //Then
        assertNotNull(product)
        assertEquals(DEFAULT_PRODUCT_ID, product.id)


    }

    @Test
    fun given_multipleProducts_when_getInventoryByIds_then_returnsRequestSubset() = runTest {
        //Given
        val products = listOf(
            productEntity { withId("id_1") },
            productEntity { withId("id_2") },
            productEntity { withId("id_3") }
        )
        dao.insertInventory(products)

        //When
        val inventory = dao.getInventoryByIds(productsIds = listOf("id_1", "id_2")).first()

        //Then
        assertTrue(inventory.any { it.id == "1" })
        assertTrue(inventory.any { it.id == "2" })
        assertTrue(inventory.none { it.id == "3" })
    }

    @Test
    fun given_oldInventory_when_replaceAll_then_onlyNewInventoryRemain() = runTest {
        //Given
        val oldProducts = listOf(
            productEntity { withId("old_1") },
            productEntity { withId("old_2") }
        )
        dao.insertInventory(oldProducts)

        val newProducts = listOf(
            productEntity { withId("new_1") },
            productEntity { withId("new_2") },
            productEntity { withId("new_3") }
        )
        dao.replaceAll(newProducts)

        //When
        val result = dao.getAllInventory().first()

        //Then
        assertEquals(newProducts.size, result.size)
        assertTrue(result.all { it.id.startsWith("new_") })
        assertTrue(result.none { it.id.startsWith("old_") })
    }

    @Test
    fun given_existingProduct_when_insertSameIdWithDifferentData_then_replaceOldData() = runTest {
        //Given
        val oldProduct = productEntity { withId(DEFAULT_PRODUCT_ID); withName("clothes") }
        dao.insertInventory(listOf(oldProduct))

        val newProduct = productEntity { withId(DEFAULT_PRODUCT_ID); withName("weights") }
        dao.insertInventory(listOf(newProduct))

        //When
        val result = dao.getAllInventory().first()

        //Then
        assertTrue(result.size == 1)
        assertEquals("weights", result.first().name)

    }

    @Test
    fun given_flowSubscribed_when_insertAfterSubscribe_then_emitsUpdateList() = runTest { //turbine

        dao.getAllInventory().test {
            val initialValue = awaitItem()
            assertTrue(initialValue.isEmpty())

            dao.insertInventory(listOf(productEntity { withId(DEFAULT_PRODUCT_ID) }))

            val updated = awaitItem()

            assertEquals(1, updated.size)
            assertEquals(DEFAULT_PRODUCT_ID, updated.first().id)

            cancelAndIgnoreRemainingEvents()
        }

    }


}
