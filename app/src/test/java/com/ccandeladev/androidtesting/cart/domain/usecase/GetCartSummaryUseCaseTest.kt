package com.ccandeladev.androidtesting.cart.domain.usecase

import com.ccandeladev.androidtesting.core.builders.DEFAULT_PRODUCT_ID
import com.ccandeladev.androidtesting.core.builders.cartItem
import com.ccandeladev.androidtesting.core.builders.offer
import com.ccandeladev.androidtesting.core.builders.product
import com.ccandeladev.androidtesting.core.fakes.FakeCartRepository
import com.ccandeladev.androidtesting.core.fakes.FakeOfferRepository
import com.ccandeladev.androidtesting.core.fakes.FakeProductRepository
import com.ccandeladev.androidtesting.core.fakes.FakeSystemClock
import com.ccandeladev.androidtesting.productlist.domain.model.OfferType
import com.ccandeladev.androidtesting.productlist.domain.usecase.GetOfferForProduct
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Instant

class GetCartSummaryUseCaseTest {

    private lateinit var clock: FakeSystemClock
    private lateinit var cartRepository: FakeCartRepository
    private lateinit var productRepository: FakeProductRepository
    private lateinit var offerRepository: FakeOfferRepository

    @Before
    fun setUp() {
        clock = FakeSystemClock().apply { setTime(Instant.parse("2026-04-03T10:00:00Z")) }
        cartRepository = FakeCartRepository()
        productRepository = FakeProductRepository()
        offerRepository = FakeOfferRepository()
    }

    private fun useCase(): GetCartSummaryUseCase {
        return GetCartSummaryUseCase(
            cartRepository = cartRepository,
            productRepository = productRepository,
            offerRepository = offerRepository,
            getOfferForProduct = GetOfferForProduct(),
            clock = clock
        )
    }

    @Test
    fun `Given percent offer when invoke then calculate correctly`() = runTest {

        //Given
        val product = product { withId(DEFAULT_PRODUCT_ID); withPrice(100.0) }
        val offer = offer {
            withProductIds(listOf(DEFAULT_PRODUCT_ID))
            withType(OfferType.PERCENT)
            withValue(10.0)
            withTimes(
                start = clock.now().minusSeconds(10),
                end = clock.now().plusSeconds(10)
            )
        }
        val cartItem = cartItem { withProductId(DEFAULT_PRODUCT_ID); withQuantity(2) }

        productRepository.setInventory(listOf(product))
        offerRepository.setOffers(listOf(offer))
        cartRepository.setCartItems(listOf(cartItem))

        //When
        val summaryResult = (useCase()()).first()

        //Then
        assertEquals(180.0, summaryResult.finalTotal)
        assertEquals(20.0, summaryResult.discountTotal)
        assertEquals(200.0, summaryResult.subTotal)

    }

    @Test
    fun `Given 3 items in offer 2x1 when invoke then only discount 1 unit`() = runTest {
        //Given
        val cart = cartItem {
            withProductId(DEFAULT_PRODUCT_ID)
            withQuantity(3)
        }
        val product = product { withId(DEFAULT_PRODUCT_ID); withPrice(100.0) }
        val offer = offer {
            withProductIds(listOf(DEFAULT_PRODUCT_ID))
            withType(OfferType.BUY_X_PAY_Y)
            withBuyQuantity(2)
            withValue(1.0) //You get one free
            withTimes(
                start = clock.now().minusSeconds(10),
                end = clock.now().plusSeconds(10)
            )
        }

        cartRepository.setCartItems(listOf(cart))
        productRepository.setInventory(listOf(product))
        offerRepository.setOffers(listOf(offer))

        //When
        val result = useCase()().first()

        //Then
        assertEquals(300.0, result.subTotal)
        assertEquals(200.0, result.finalTotal)
        assertEquals(100.0, result.discountTotal)

    }

    @Test
    fun `Given multiple products with different offers when invoke then sums all correctly`() =
        runTest {
            //Given
            val now = clock.now()
            val p1 = product { //with offer
                withId("P-1"); withPrice(100.0)
            }
            val p2 = product { // without offer
                withId("P-2"); withPrice(200.0)
            }
            val offerP1 = offer {
                withProductIds(listOf("P-1"))
                withType(OfferType.PERCENT)
                withValue(10.0)
                withTimes(
                    start = now.minusSeconds(10),
                    end = now.plusSeconds(10)
                )
            }
            val cart = listOf(
                cartItem { withProductId("P-1"); withQuantity(1) },
                cartItem { withProductId("P-2"); withQuantity(1) }
            )

            productRepository.setInventory(listOf(p1, p2))
            offerRepository.setOffers(listOf(offerP1))
            cartRepository.setCartItems(cart)

            //When
            val resultSummary = useCase()().first()

            //Then
            assertEquals(290.0, resultSummary.finalTotal)
            assertEquals(300.0, resultSummary.subTotal)
            assertEquals(10.0, resultSummary.discountTotal)

        }

    @Test
    fun `Given offer out of date when invoke then discount is zero`() =
        runTest {
            //Given
            val now = clock.now()
            val product = product { withId(DEFAULT_PRODUCT_ID); withPrice(100.0) }
            val offer = offer {
                withProductIds(listOf(DEFAULT_PRODUCT_ID))
                withType(OfferType.PERCENT)
                withValue(10.0)
                withTimes(
                    start = now.minusSeconds(30),
                    end = now.minusSeconds(10)
                )
            }
            val cart = cartItem { withProductId(DEFAULT_PRODUCT_ID); withQuantity(1) }

            productRepository.setInventory(listOf(product))
            offerRepository.setOffers(listOf(offer))
            cartRepository.setCartItems(listOf(cart))

            //When
            val summaryResult = useCase()().first()

            //Then
            assertEquals(100.0, summaryResult.finalTotal)
            assertEquals(100.0, summaryResult.subTotal)
            assertEquals(0.0, summaryResult.discountTotal)


        }

    @Test
    fun `Given active offer when time advance then summary update automatically`() =
        runTest {
            //Given
            val now = clock.now()
            val product = product { withId(DEFAULT_PRODUCT_ID); withPrice(100.0) }
            val offer = offer {
                withProductIds(listOf(DEFAULT_PRODUCT_ID))
                withType(OfferType.PERCENT)
                withValue(10.0)
                withTimes(
                    start = now.minusSeconds(30),
                    end = now.plusSeconds(5)
                )
            }
            val cart = cartItem { withProductId(DEFAULT_PRODUCT_ID); withQuantity(1) }

            productRepository.setInventory(listOf(product))
            offerRepository.setOffers(listOf(offer))
            cartRepository.setCartItems(listOf(cart))

            //When-Then
            val summaryFlow = useCase()()

            assertEquals(90.0, summaryFlow.first().finalTotal)
            assertEquals(10.0, summaryFlow.first().discountTotal)
            clock.advanceTime(6)
            assertEquals(100.0, summaryFlow.first().finalTotal)
            assertEquals(0.0, summaryFlow.first().discountTotal)

        }


}






























