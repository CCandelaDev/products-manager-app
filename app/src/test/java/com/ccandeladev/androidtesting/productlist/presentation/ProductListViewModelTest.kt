package com.ccandeladev.androidtesting.productlist.presentation

import app.cash.turbine.turbineScope
import com.ccandeladev.androidtesting.core.builders.DEFAULT_PRODUCT_ID
import com.ccandeladev.androidtesting.core.builders.product
import com.ccandeladev.androidtesting.core.fakes.FakeOfferRepository
import com.ccandeladev.androidtesting.core.fakes.FakeProductRepository
import com.ccandeladev.androidtesting.core.fakes.FakeSettingsRepository
import com.ccandeladev.androidtesting.core.fakes.FakeSystemClock
import com.ccandeladev.androidtesting.core.maindispatchersrule.MainDispatcherRule
import com.ccandeladev.androidtesting.core.stubs.FailingProductRepositoryStub
import com.ccandeladev.androidtesting.productlist.domain.model.SortOption
import com.ccandeladev.androidtesting.productlist.domain.repository.ProductRepository
import com.ccandeladev.androidtesting.productlist.domain.usecase.GetInventoryUseCase
import com.ccandeladev.androidtesting.productlist.domain.usecase.GetOfferForProduct
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class ProductListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun createViewmodel(
        fakeProduct: ProductRepository = FakeProductRepository(),
        fakeSettings: FakeSettingsRepository = FakeSettingsRepository(),
        fakeOffer: FakeOfferRepository = FakeOfferRepository(),
        fakeClock: FakeSystemClock = FakeSystemClock()
    ): ProductListViewModel {

        val getInventoryUseCase = GetInventoryUseCase(
            productRepository = fakeProduct,
            offerRepository = fakeOffer,
            getOfferForProduct = GetOfferForProduct(),
            settingsRepository = fakeSettings,
            clock = fakeClock
        )

        return ProductListViewModel(
            getInventoryUseCase = getInventoryUseCase,
            settingsRepository = fakeSettings
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testInitializationEmitsSuccessState() =
        runTest(mainDispatcherRule.scheduler) {

            turbineScope {
                //Given
                val product1 = product { withId(DEFAULT_PRODUCT_ID) }
                val fakeProduct = FakeProductRepository().apply { setInventory(listOf(product1)) }

                //When: Only modify the FakeProductRepository
                val viewModel = createViewmodel(fakeProduct = fakeProduct)
                val state = viewModel.uiState.testIn(this)

                //Then
                val emittedState = state.awaitItem()//first flow, initial value(Loading)

                assertTrue(emittedState is ProductListUiState.Success)
                assertEquals(1, (emittedState as ProductListUiState.Success).inventory.size)

                state.cancelAndIgnoreRemainingEvents()
            }
        }


    @Test
    fun `given selected category when set category then filters products`() =
        runTest(mainDispatcherRule.scheduler) {

            turbineScope {
                val p1 = product { withId("p1"); withCategory("Footwear") }
                val p2 = product { withId("p2"); withCategory("Clothing") }

                val fakeProduct = FakeProductRepository().apply { setInventory(listOf(p1, p2)) }

                val viewModel = createViewmodel(fakeProduct = fakeProduct)
                val state = viewModel.uiState.testIn(this)
                state.awaitItem()
                viewModel.setCategory("Footwear")

                val updateState = state.awaitItem()
                assertTrue(updateState is ProductListUiState.Success)
                assertEquals(1, (updateState as ProductListUiState.Success).inventory.size)
                assertEquals("Footwear", updateState.selectedCategory)

                state.cancelAndIgnoreRemainingEvents()

            }

        }


    @Test
    fun `given price asc sort option when set sort then sorts by effective price`() =
        runTest(mainDispatcherRule.scheduler) {

            turbineScope {
                val p1 = product { withId("p1"); withPrice(100.0) }
                val p2 = product { withId("p2"); withPrice(200.0) }

                val fakeProduct = FakeProductRepository().apply { setInventory(listOf(p1, p2)) }

                val viewModel = createViewmodel(fakeProduct = fakeProduct)
                val state = viewModel.uiState.testIn(this)
                state.awaitItem()
                viewModel.setSortOption(SortOption.PRICE_ASC)

                val updateState = state.awaitItem()
                assertTrue(updateState is ProductListUiState.Success)
                assertEquals(2, (updateState as ProductListUiState.Success).inventory.size)
                assertEquals(SortOption.PRICE_ASC, updateState.sortOption)
                assertEquals(100.0, updateState.inventory[0].product.price)
                assertEquals(200.0, updateState.inventory[1].product.price)


                state.cancelAndIgnoreRemainingEvents()
            }

        }

    // Stub: Test that verifies the ViewModel emits an Error state when the repository throws an exception during product loading.
    // This ensures proper error handling in the UI layer when data fetching fails.
    @Test
    fun `given repository error when loading products then emits error state`() =
        runTest(mainDispatcherRule.scheduler) {

            turbineScope {
                val failingRepository =
                    FailingProductRepositoryStub(exception = Exception("Proof test"))

                val viewModel = createViewmodel(fakeProduct = failingRepository)
                val state = viewModel.uiState.testIn(this)
                val emittedState = state.awaitItem()

                assertTrue(emittedState is ProductListUiState.Error)
                assertTrue((emittedState as ProductListUiState.Error).message == "Proof test")
                //Workaround
                if (emittedState is ProductListUiState.Error) {
                    assertEquals("Proof test", emittedState.message)
                }

                state.cancelAndIgnoreRemainingEvents()
            }

        }


}