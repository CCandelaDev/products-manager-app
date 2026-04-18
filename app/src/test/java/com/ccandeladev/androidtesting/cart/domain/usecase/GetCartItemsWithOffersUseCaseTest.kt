package com.ccandeladev.androidtesting.cart.domain.usecase

import com.ccandeladev.androidtesting.core.builders.DEFAULT_PRODUCT_ID
import com.ccandeladev.androidtesting.core.builders.cartItem
import com.ccandeladev.androidtesting.core.builders.offer
import com.ccandeladev.androidtesting.core.builders.product
import com.ccandeladev.androidtesting.core.fakes.FakeCartRepository
import com.ccandeladev.androidtesting.core.fakes.FakeOfferRepository
import com.ccandeladev.androidtesting.core.fakes.FakeProductRepository
import com.ccandeladev.androidtesting.core.fakes.FakeSystemClock
import com.ccandeladev.androidtesting.productlist.domain.usecase.GetOfferForProduct
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Instant

class GetCartItemsWithOffersUseCaseTest {

    private val clock = FakeSystemClock().apply { setTime(Instant.parse("2026-04-03T10:00:00Z")) }

    private fun useCase(
        cartRepository: FakeCartRepository = FakeCartRepository(),
        productRepository: FakeProductRepository = FakeProductRepository(),
        offerRepository: FakeOfferRepository = FakeOfferRepository(),
        clock: FakeSystemClock = this.clock

    ): GetCartItemsWithOffersUseCase {
        return GetCartItemsWithOffersUseCase(
            cartRepository,
            productRepository,
            offerRepository,
            GetOfferForProduct(),
            clock

        )
    }

    @Test
    fun `given empty cart when invokes then return empty list`() = runTest {

        //Given
        val cart = FakeCartRepository().apply { setCartItems(emptyList()) }

        //When
        val result = (useCase(cartRepository = cart)()).first()

        //Then
        assertTrue("Initial state must be empty", result.isEmpty())
    }


    @Test
    fun `given existing cart item with active offer when invoke then returns item with offer`() =
        runTest {

            val product = product {
                withId(DEFAULT_PRODUCT_ID)
            }

            val now = clock.now()

            val offer = offer {
                withProductIds(listOf(DEFAULT_PRODUCT_ID))
                withTimes(
                    start = now.minusSeconds(60),
                    end = now.plusSeconds(30)
                )
            }

            val cartItem = cartItem {
                withProductId(DEFAULT_PRODUCT_ID)
                withQuantity(2)
            }

            val cart = FakeCartRepository().apply { setCartItems(listOf(cartItem)) }
            val products = FakeProductRepository().apply { setInventory(listOf(product)) }
            val offers = FakeOfferRepository().apply { setOffers(listOf(offer)) }

            //When
            val result = useCase(cart, products, offers)().first()

            //Then
            assertEquals(1, result.size)
            assertNotNull(result.first().item.offer)

        }

    @Test
    fun `given cart item without matching product when invoke then skip item`() =
        runTest {
            //Given
            val cartItem = cartItem {
                withProductId("ghost-id")
            }
            val product = product {
                withId(DEFAULT_PRODUCT_ID)
            }

            val cart = FakeCartRepository().apply { setCartItems(listOf(cartItem)) }
            val products =
                FakeProductRepository().apply { setInventory(listOf(product)) } // Not match with cartItem productId

            //When
            val result = (useCase(cart, products))().first()

            //Then
            assertTrue(result.isEmpty())
        }

    @Test
    fun `given offer ending exacting now when invoke then it must be included`() =
        runTest {
            //Given
            val now = clock.now()
            val endingOffer = offer {
                withProductIds(listOf(DEFAULT_PRODUCT_ID))
                withTimes(start = now.minusSeconds(60), end = now)
            }

            val cartItem = cartItem {
                withProductId(DEFAULT_PRODUCT_ID)
            }

            val product = product {
                withId(DEFAULT_PRODUCT_ID)
            }

            val cart = FakeCartRepository().apply { setCartItems(listOf(cartItem)) }
            val products =
                FakeProductRepository().apply { setInventory(listOf(product)) } // Not match with cartItem productId
            val offers = FakeOfferRepository().apply { setOffers(listOf(endingOffer)) }

            //When
            val result = (useCase(cart, products, offers))().first()

            //Then
            assertNotNull(result.first().item.offer)
            assertEquals(1, result.size)
        }

    @Test
    fun `given expired offer when invoke then remains but without offer`() =
        runTest {
            //Given
            val now = clock.now()
            val endOffer = offer {
                withProductIds(listOf(DEFAULT_PRODUCT_ID))
                withTimes(start = now.minusSeconds(60), end = now.minusSeconds(10))
            }

            val cartItem = cartItem {
                withProductId(DEFAULT_PRODUCT_ID)
            }

            val product = product {
                withId(DEFAULT_PRODUCT_ID)
            }

            val cart = FakeCartRepository().apply { setCartItems(listOf(cartItem)) }
            val products =
                FakeProductRepository().apply { setInventory(listOf(product)) } // Not match with cartItem productId
            val offers = FakeOfferRepository().apply { setOffers(listOf(endOffer)) }

            //When
            val result = (useCase(cart, products, offers))().first()

            //Then
            assertNull(result.first().item.offer) //Because offer has expired, then it's null

        }

    @Test
    fun `given active offer when time advance then flow emits update list without offer`() =
        runTest {
            //Given
            val now = clock.now()
            val Offer = offer {
                withProductIds(listOf(DEFAULT_PRODUCT_ID))
                withTimes(start = now.minusSeconds(60), end = now.plusSeconds(5))
            }

            val cartItem = cartItem {
                withProductId(DEFAULT_PRODUCT_ID)
            }

            val product = product {
                withId(DEFAULT_PRODUCT_ID)
            }

            val cart = FakeCartRepository().apply { setCartItems(listOf(cartItem)) }
            val products =
                FakeProductRepository().apply { setInventory(listOf(product)) } // Not match with cartItem productId
            val offers = FakeOfferRepository().apply { setOffers(listOf(Offer)) }

            //When-Then
            val myUseCase = useCase(cart, products, offers)()

            val firstEmission = myUseCase.first()
            assertNotNull(firstEmission.first().item.offer) //Offer is active

            clock.advanceTime(6) // we advance time six seconds

            val secondEmission = myUseCase.first()
            assertNull(secondEmission.first().item.offer) // Offer is ended


        }

}