package com.ccandeladev.androidtesting.productlist.data.local.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.ccandeladev.androidtesting.core.builders.DEFAULT_OFFER_ID
import com.ccandeladev.androidtesting.core.builders.offerEntity
import com.ccandeladev.androidtesting.core.data.local.database.ProductManagerDatabase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class OfferDaoTest {

    private lateinit var database: ProductManagerDatabase
    private lateinit var dao: OfferDao

    @Before
    fun setUp() {

        database =
            Room.inMemoryDatabaseBuilder(
                context = ApplicationProvider.getApplicationContext(),
                klass = ProductManagerDatabase::class.java
            ).build()

        dao = database.offerDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun givenOffer_whenInsertOffer_thenOfferPersisted() = runTest {
        //Given
        dao.insertOffers(listOf(offerEntity {
            withId(DEFAULT_OFFER_ID)
            withPercent(10)
            withType("Weight")
        }))
        //When
        val result = dao.getAllOffers().first()
        //Then
        assertTrue(result.size == 1)
        assertEquals(DEFAULT_OFFER_ID, result.first().id)
        assertEquals(10, result.first().percent)
        assertEquals("Weight", result.first().type)
    }

    @Test
    fun givenPersistedOffers_whenDeleteAll_thenDatabaseIsEmpty() = runTest {
        val offer1 = offerEntity { withId("id_1") }
        val offer2 = offerEntity { withId("id_2") }
        dao.insertOffers(listOf(offer1, offer2))

        dao.clearOffers()
        val result = dao.getAllOffers().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun givenExistingOffers_whenReplaceAll_thenOnlyNewOffersPersisted() = runTest {
        val oldOffer = offerEntity { withId("id_old") }
        val newOffer = offerEntity { withId("id_new") }
        dao.insertOffers(listOf(oldOffer))

        dao.replaceAll(listOf(newOffer))

        val result = dao.getAllOffers().first()
        assertTrue(result.size == 1)
        assertEquals("id_new", result.first().id)
    }

    @Test
    fun givenExistingOffer_whenInsertSameId_thenUpdatedExistingOffer() = runTest {
        val oldOffer = offerEntity { withId(DEFAULT_OFFER_ID); withPercent(10) }
        val newOffer = offerEntity { withId(DEFAULT_OFFER_ID); withPercent(20) }

        dao.insertOffers(listOf(oldOffer))
        dao.insertOffers(listOf(newOffer))

        val result = dao.getAllOffers().first()
        assertTrue(result.size == 1)
        assertEquals(DEFAULT_OFFER_ID, result.first().id)
        assertEquals(20, result.first().percent)

    }

}