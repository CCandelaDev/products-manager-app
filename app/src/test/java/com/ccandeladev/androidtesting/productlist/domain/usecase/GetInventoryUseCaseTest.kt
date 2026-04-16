package com.ccandeladev.androidtesting.productlist.domain.usecase

import com.ccandeladev.androidtesting.core.builders.offer
import com.ccandeladev.androidtesting.core.builders.product
import com.ccandeladev.androidtesting.core.fakes.FakeOfferRepository
import com.ccandeladev.androidtesting.core.fakes.FakeProductRepository
import com.ccandeladev.androidtesting.core.fakes.FakeSettingsRepository
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Instant

class GetInventoryUseCaseTest {

    private fun useCase(
        product: FakeProductRepository = FakeProductRepository(),
        offer: FakeOfferRepository = FakeOfferRepository(),
        settings: FakeSettingsRepository = FakeSettingsRepository()
    ) = GetInventoryUseCase(
        product,
        offer,
        GetOfferForProduct(),
        settings,

        )

    @Test
    fun `given offer ending now when invoke then it should be included`() = runTest {
        //Given
        val now = Instant.now()

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
        val result = (useCase(product = productRepository, offer = offerRepository)()).first()

        //Then : to verify offer is still valid
        //Flaky test: If the test takes too long, it will fail because of the `end = now`.
        assertNotNull(result.first())
    }

}