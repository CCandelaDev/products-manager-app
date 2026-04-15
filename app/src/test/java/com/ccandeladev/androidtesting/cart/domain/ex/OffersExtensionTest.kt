package com.ccandeladev.androidtesting.cart.domain.ex

import com.ccandeladev.androidtesting.core.builders.offer
import com.ccandeladev.androidtesting.productlist.domain.model.Offer
import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.time.Instant


class OffersExtensionTest {
    private val now = Instant.parse("2026-04-03T10:00:00Z")

    @Test
    fun givenFutureOffer_whenActiveAt_thenExclude() {
        //Given
        val futureOffer = offer {
            withTimes(
                start = now.plusSeconds(10),
                end = now.plusSeconds(100)
            )
        }
        val offers = listOf(futureOffer)

        //When
        val result = offers.activeAt(now = now)

        //Then: Don`t have active offers actually
        assertEquals(0, result.size)
    }

    @Test
    fun givenExpiredOffer_whenActiveAt_thenExclude() {
        //Given
        val expiredOffer =
            offer {
                withTimes(
                    start = now.minusSeconds(100),
                    end = now.minusSeconds(10)
                )
            }
        val offers = listOf(expiredOffer)

        //When
        val result = offers.activeAt(now)

        //Then
        assertEquals(0, result.size)
    }

    @Test
    fun givenActiveOffer_whenActiveAt_thenActive() {
        //Given
        val activeOffer = offer {
            withTimes(
                start = now.minusSeconds(10),
                end = now.plusSeconds(10)
            )
        }
        val offers = listOf(activeOffer)

        //When
        val result = offers.activeAt(now = now)

        //Then
        assertEquals(1, result.size)
    }

    @Test
    fun givenExactStartTimeOffer_whenActiveAt_thenActive() {
        //Given
        val startNowOffer = offer {
            withTimes(start = now, end = now.plusSeconds(100))
        }
        val offers = listOf(startNowOffer)

        //When
        val result = offers.activeAt(now = now)

        //Then
        assertEquals(1, result.size)

    }

    @Test
    fun givenExactEndTimeOffer_whenActiveAt_thenActive() {
        //Given
        val startNowOffer = offer {
            withTimes(
                start = now.minusSeconds(100),
                end = now
            )
        }
        val offers = listOf(startNowOffer)

        //When
        val result = offers.activeAt(now = now)

        //Then
        assertEquals(1, result.size)
    }

    @Test
    fun givenEmptyList_whenActiveAt_thenEmpty(){
        //Given
        val offers =  emptyList<Offer>()

        //When
        val result = offers.activeAt(now = now)

        //Then
        assertEquals(0, result.size)
    }

}