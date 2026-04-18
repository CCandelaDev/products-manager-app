package com.ccandeladev.androidtesting.productlist.domain.usecase

import com.ccandeladev.androidtesting.core.builders.offer
import com.ccandeladev.androidtesting.core.builders.product
import com.ccandeladev.androidtesting.core.data.util.SystemClock
import com.ccandeladev.androidtesting.core.fakes.FakeOfferRepository
import com.ccandeladev.androidtesting.core.fakes.FakeProductRepository
import com.ccandeladev.androidtesting.core.fakes.FakeSettingsRepository
import com.ccandeladev.androidtesting.core.fakes.FakeSystemClock
import com.ccandeladev.androidtesting.productlist.domain.repository.OfferRepository
import com.ccandeladev.androidtesting.productlist.domain.repository.ProductRepository
import com.ccandeladev.androidtesting.productlist.domain.repository.SettingsRepository
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Instant

class GetInventoryUseCaseTest {

    private fun useCase(
        product: FakeProductRepository = FakeProductRepository(),
        offer: FakeOfferRepository = FakeOfferRepository(),
        settings: FakeSettingsRepository = FakeSettingsRepository(),
        clock: FakeSystemClock = FakeSystemClock()
    ) = GetInventoryUseCase(
        product,
        offer,
        GetOfferForProduct(),
        settings,
        clock = clock
    )

    @Test
    fun `given offer ending now when invoke then it should be included`() = runTest {
        //Given
        //val now = Instant.now()  //Don't have date hardcoding
        //Correct, deterministic test
        val now = Instant.parse("2026-04-03T10:00:00Z")
        val clock = FakeSystemClock().apply {
            setTime(now)
        }

        val productId = "product-id"
        val product = product { withId(productId) }
        val offer = offer {
            withProductIds(listOf(productId))
            withTimes(start = now.minusSeconds(60), end = now)
        }

        // To set inventory and offers
        val productRepository = FakeProductRepository().apply { setInventory(listOf(product)) }
        val offerRepository = FakeOfferRepository().apply { setOffers(listOf(offer)) }

        //When : List of offers
        val result =
            (useCase(product = productRepository, offer = offerRepository, clock = clock)()).first()

        //Then : to verify offer is still valid
        //Flaky test: If the test takes too long, it will fail because of the `end = now`.
        assertNotNull(result.first())
    }


    @Test
    fun `given active offer when time advances then offer should no be longer be returned`() =
        runTest {
            //Given
            //Correct, deterministic test
            val now = Instant.parse("2026-04-03T10:00:00Z")
            val clock = FakeSystemClock().apply {
                setTime(now)
            }

            val productId = "product-id"
            val product = product { withId(productId) }
            val offer = offer {
                withProductIds(listOf(productId))
                withTimes(start = now, end = now.plusSeconds(5))
            }

            // To set inventory and offers
            val productRepository = FakeProductRepository().apply { setInventory(listOf(product)) }
            val offerRepository = FakeOfferRepository().apply { setOffers(listOf(offer)) }

            //When
            val firstResult = (useCase(
                product = productRepository,
                offer = offerRepository,
                clock = clock
            ))().first()

            clock.advanceTime(6)  //Advance 6 seconds... offer finished

            val secondResult = (useCase(
                product = productRepository,
                offer = offerRepository,
                clock = clock
            ))().first()

            //Then
            assertNotNull(firstResult.first().offer)
            assertNull(secondResult.first().offer)

        }

    @Test
    fun `given inStockOnly enabled when product goes out of stock then it should be filtered`() =
        runTest {
            //Given
            val productId = "product-id"
            val product = product {
                withId(productId)
                withStock(0)
            }

            val settings = FakeSettingsRepository().apply { setInStockOnly(true) }
            val productRepository = FakeProductRepository().apply { setInventory(listOf(product)) }

            val myUseCase = useCase(product = productRepository, settings = settings)

            //When
            val result = myUseCase().first()

            //Then
            assertTrue("List must be empty when inStockOnly is active", result.isEmpty())
        }

    @Test
    fun `mock given inStockOnly enabled when product goes out of stock then it should be filtered`() =
        runTest {

            val productRepository = mockk<ProductRepository>()
            val settingsRepository = mockk<SettingsRepository>()
            val offerRepository = mockk<OfferRepository>()
            val clock = mockk<SystemClock>()

            val now = Instant.parse("2026-04-03T10:00:00Z")

            //Given
            val productId = "product-id"
            val product = product {
                withId(productId)
                withStock(0)
            }

            //To program the behavior of the mocks
            every { settingsRepository.inStockOnly } returns flowOf(true)
            every { productRepository.getInventory() } returns flowOf(listOf(product))
            every { offerRepository.getActiveOffers() } returns flowOf(emptyList())
            every { clock.now() } returns now


            val myUseCase = GetInventoryUseCase(
                productRepository = productRepository,
                getOfferForProduct = GetOfferForProduct(),
                settingsRepository = settingsRepository,
                offerRepository = offerRepository,
                clock = clock
            )

            //When
            val result = myUseCase().first()

            //Then
            assertTrue(result.isEmpty())
        }

}