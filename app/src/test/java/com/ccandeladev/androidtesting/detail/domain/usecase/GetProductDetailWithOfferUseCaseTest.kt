package com.ccandeladev.androidtesting.detail.domain.usecase

import com.ccandeladev.androidtesting.core.builders.DEFAULT_PRODUCT_ID
import com.ccandeladev.androidtesting.core.builders.offer
import com.ccandeladev.androidtesting.core.builders.product
import com.ccandeladev.androidtesting.core.fakes.FakeOfferRepository
import com.ccandeladev.androidtesting.core.fakes.FakeProductRepository
import com.ccandeladev.androidtesting.core.fakes.FakeSystemClock
import com.ccandeladev.androidtesting.productlist.domain.model.OfferType
import com.ccandeladev.androidtesting.productlist.domain.model.ProductOffer
import com.ccandeladev.androidtesting.productlist.domain.usecase.GetOfferForProduct
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Instant

class GetProductDetailWithOfferUseCaseTest {

    private lateinit var clock: FakeSystemClock
    private lateinit var productRepository: FakeProductRepository
    private lateinit var offerRepository: FakeOfferRepository


    @Before
    fun setUp() {
        clock = FakeSystemClock().apply { setTime(Instant.parse("2026-04-03T10:00:00Z")) }
        productRepository = FakeProductRepository()
        offerRepository = FakeOfferRepository()
    }

    fun useCase(): GetProductDetailWithOfferUseCase {
        return GetProductDetailWithOfferUseCase(
            clock = clock,
            productRepository = productRepository,
            offerRepository = offerRepository,
            getOfferForProduct = GetOfferForProduct()
        )
    }

    @Test
    fun `given existing product and active offer when invoke then returns product with associated offer`() =
        runTest() {
            //Given
            val now = clock.now()
            val product = product { withId(DEFAULT_PRODUCT_ID); withPrice(100.0) }
            val offer = offer {
                withProductIds(listOf(DEFAULT_PRODUCT_ID))
                withType(OfferType.PERCENT)
                withValue(10.0)
                withTimes(
                    start = now.minusSeconds(10),
                    end = now.plusSeconds(10)
                )
            }

            productRepository.setInventory(listOf(product))
            offerRepository.setOffers(listOf(offer))

            //When
            val result = useCase()(productId = DEFAULT_PRODUCT_ID).first()

            //Then
            assertNotNull(result)
            assertEquals(DEFAULT_PRODUCT_ID, result?.product?.id)
            assertNotNull(result?.offer)

            result?.offer as ProductOffer.Percent
            assertEquals(10.0, result.offer.percent)
            assertEquals(90.0, result.offer.discountedPrice)


        }


    @Test
    fun `given expired offer when invoke then returns product without offer`() =
        runTest() {
            //Given
            val now = clock.now()
            val product = product { withId(DEFAULT_PRODUCT_ID); withPrice(100.0) }
            val offer = offer {
                withProductIds(listOf(DEFAULT_PRODUCT_ID))
                withType(OfferType.PERCENT)
                withValue(10.0)
                withTimes(
                    start = now.minusSeconds(10),
                    end = now.minusSeconds(5)
                )
            }

            productRepository.setInventory(listOf(product))
            offerRepository.setOffers(listOf(offer))

            //When
            val result = useCase()(productId = DEFAULT_PRODUCT_ID).first()

            //Then
            assertNotNull(result)
            assertEquals(DEFAULT_PRODUCT_ID, result?.product?.id)

            assertNull(result?.offer)

        }


    @Test
    fun `given inexistent productId  when invoke then returns null`() =
        runTest() {
            //Given
            val now = clock.now()
            val product = product { withId(DEFAULT_PRODUCT_ID); withPrice(100.0) }
            val offer = offer {
                withProductIds(listOf(DEFAULT_PRODUCT_ID))
                withType(OfferType.PERCENT)
                withValue(10.0)
                withTimes(
                    start = now.minusSeconds(10),
                    end = now.minusSeconds(5)
                )
            }

            productRepository.setInventory(listOf(product))
            offerRepository.setOffers(listOf(offer))

            //When
            val result = useCase()(productId = "non-existent-id").first()

            //Then
            assertNull(result)
        }

    @Test
    fun `given active offer when time advances beyond end time then emits updated product with null offer`() =
        runTest() {
            //Given
            val now = clock.now()
            val product = product { withId(DEFAULT_PRODUCT_ID); withPrice(100.0) }
            val offer = offer {
                withProductIds(listOf(DEFAULT_PRODUCT_ID))
                withType(OfferType.PERCENT)
                withValue(10.0)
                withTimes(
                    start = now.minusSeconds(10),
                    end = now.plusSeconds(5)
                )
            }

            productRepository.setInventory(listOf(product))
            offerRepository.setOffers(listOf(offer))

            //When
            val resultFlow = useCase()(productId = DEFAULT_PRODUCT_ID)

            //Then
            assertNotNull(resultFlow.first()?.offer)

            clock.advanceTime(6)

            assertNull(resultFlow.first()?.offer)

        }

    @Test
    fun `Given offer exactly at start time when invoke then it is included as active`() =
        runTest {
            //Given
            val now = clock.now()
            val product = product { withId(DEFAULT_PRODUCT_ID); withPrice(100.0) }
            val offer = offer {
                withProductIds(listOf(DEFAULT_PRODUCT_ID))
                withType(OfferType.PERCENT)
                withValue(10.0)
                withTimes(
                    start = now,
                    end = now.plusSeconds(10)
                )
            }

            productRepository.setInventory(listOf(product))
            offerRepository.setOffers(listOf(offer))

            //When
            val result = useCase()(productId = DEFAULT_PRODUCT_ID).first()

            //Then
            assertNotNull(result?.offer)
        }

}























