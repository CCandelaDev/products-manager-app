package com.ccandeladev.androidtesting.productlist.presentation

import com.ccandeladev.androidtesting.core.fakes.FakeOfferRepository
import com.ccandeladev.androidtesting.core.fakes.FakeProductRepository
import com.ccandeladev.androidtesting.core.fakes.FakeSettingsRepository
import com.ccandeladev.androidtesting.core.fakes.FakeSystemClock
import com.ccandeladev.androidtesting.core.maindispatchersrule.MainDispatcherRule
import com.ccandeladev.androidtesting.productlist.domain.model.SortOption
import com.ccandeladev.androidtesting.productlist.domain.repository.ProductRepository
import com.ccandeladev.androidtesting.productlist.domain.repository.SettingsRepository
import com.ccandeladev.androidtesting.productlist.domain.usecase.GetInventoryUseCase
import com.ccandeladev.androidtesting.productlist.domain.usecase.GetOfferForProduct
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class ProductListViewModelMockTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val settingsRepository: SettingsRepository = mockk(relaxed = true) {
        every { selectCategory } returns flowOf(null)
        every { sortOption } returns flowOf(SortOption.NONE)
        every { inStockOnly } returns flowOf(false)
        every { filtersVisible } returns flowOf(true)
    }

    private fun createViewModel(
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
            settingsRepository = settingsRepository,
            getInventoryUseCase = getInventoryUseCase
        )
    }

    //   Fakes: Verify "What happens?" - They test the logic and data flow
    //   Mocks: Verify "How is it called?" - They test method calls and interactions
    @Test
    fun `given category when set category then delegates to settings repository`() =
        runTest(mainDispatcherRule.scheduler) {

            //Given
            val viewModel = createViewModel()
            val category = "Footwear"

            //When
            viewModel.setCategory(category = category)

            //Then
            coVerify(exactly = 1) { settingsRepository.setSelectCategory(category) }

        }

    @Test
    fun `given sort option when set sort option then delegates to settings repository`() =
        runTest(mainDispatcherRule.scheduler) {

            //Given
            val viewModel = createViewModel()
            val option = SortOption.DISCOUNT

            //When
            viewModel.setSortOption(option)

            //Then
            coVerify(exactly = 1) { settingsRepository.setSortOption(option) }

        }

    @Test
    fun `given filter visible when set filter visible then delegates to settings repository`() =
        runTest(mainDispatcherRule.scheduler) {

            //Given
            val viewModel = createViewModel()
            val filtersVisible = true

            //When
            viewModel.setFilterVisible(filtersVisible)

            //Then
            coVerify(exactly = 1) { settingsRepository.setFiltersVisible(filtersVisible) }

        }


}