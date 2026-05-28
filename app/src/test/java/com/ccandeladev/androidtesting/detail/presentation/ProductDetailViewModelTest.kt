package com.ccandeladev.androidtesting.detail.presentation

import app.cash.turbine.test
import com.ccandeladev.androidtesting.cart.domain.usecase.AddToCartUseCase
import com.ccandeladev.androidtesting.core.builders.DEFAULT_PRODUCT_ID
import com.ccandeladev.androidtesting.core.builders.product
import com.ccandeladev.androidtesting.core.domain.model.AppError
import com.ccandeladev.androidtesting.core.domain.util.Clock
import com.ccandeladev.androidtesting.core.fakes.FakeCartRepository
import com.ccandeladev.androidtesting.core.fakes.FakeOfferRepository
import com.ccandeladev.androidtesting.core.fakes.FakeProductRepository
import com.ccandeladev.androidtesting.core.fakes.FakeSystemClock
import com.ccandeladev.androidtesting.core.maindispatchersrule.MainDispatcherRule
import com.ccandeladev.androidtesting.detail.domain.usecase.GetProductDetailWithOfferUseCase
import com.ccandeladev.androidtesting.productlist.domain.usecase.GetOfferForProduct
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class ProductDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    fun createViewModel(
        fakeProductRepository: FakeProductRepository = FakeProductRepository(),
        fakeOfferRepository: FakeOfferRepository = FakeOfferRepository(),
        fakeCartRepository: FakeCartRepository = FakeCartRepository(),
        getOfferForProduct: GetOfferForProduct = GetOfferForProduct(),
        clock: Clock = FakeSystemClock()

    ): ProductDetailViewModel {

        val getProductDetailWithOfferUseCase =
            GetProductDetailWithOfferUseCase(
                productRepository = fakeProductRepository,
                offerRepository = fakeOfferRepository,
                getOfferForProduct = getOfferForProduct,
                clock = clock
            )
        val addToCartUseCase = AddToCartUseCase(
            cartRepository = fakeCartRepository,
            productRepository = fakeProductRepository
        )

        return ProductDetailViewModel(
            getProductDetailWithOfferUseCase = getProductDetailWithOfferUseCase,
            addToCartUseCase = addToCartUseCase
        )
    }

    @Test
    fun `given valid productid when setProductid called then uiState emits loading and success`() =
        runTest {
            //Given
            val product = product { withId(DEFAULT_PRODUCT_ID) }
            val fakeProductRepository =
                FakeProductRepository().apply { setInventory(listOf(product)) }

            val viewModel = createViewModel(fakeProductRepository = fakeProductRepository)


            //Then
            viewModel.uiState.test {
                viewModel.setProductId(DEFAULT_PRODUCT_ID)
                //First: loading state
                assertTrue(awaitItem() is ProductDetailUiState.Loading)

                //second: success state
                val successState = awaitItem() as ProductDetailUiState.Success
                assertNotNull(successState.item)
                assertEquals(DEFAULT_PRODUCT_ID, successState.item?.product?.id)

            }
        }

    @Test
    fun `given network error when loading product then uiState emits error`() =
        runTest {

            //Mockk
            val mockUseCase = mockk<GetProductDetailWithOfferUseCase>()
            every { mockUseCase(DEFAULT_PRODUCT_ID) } returns flow { throw AppError.NetworkError }

            val viewModel = ProductDetailViewModel(
                getProductDetailWithOfferUseCase = mockUseCase,
                addToCartUseCase = mockk(relaxed = true)
            )


            //Then: uiState stops being in  Loading state
            viewModel.uiState.test {
                viewModel.setProductId(DEFAULT_PRODUCT_ID)
                assertTrue(awaitItem() is ProductDetailUiState.Loading)
                assertTrue(awaitItem() is ProductDetailUiState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given product loaded when add to cart called then emits SUCCESS_ADD_TO_CART event`() =
        runTest(mainDispatcherRule.scheduler) {

            //Given
            val product = product { withId(DEFAULT_PRODUCT_ID) }
            val fakeProductRepository =
                FakeProductRepository().apply { setInventory(listOf(product)) }

            //When
            val viewModel = createViewModel(
                fakeProductRepository = fakeProductRepository
            )

            //wait until the product is charged
            viewModel.uiState.test {
                viewModel.setProductId(DEFAULT_PRODUCT_ID)
                awaitItem() //loading
                awaitItem() //success with product
                cancelAndIgnoreRemainingEvents()
            }

            //Then
            // the product is at uiState
            viewModel.events.test {
                viewModel.addToCart()
                val event = awaitItem()
                assertEquals(ProductDetailEvent.SUCCESS_ADD_TO_CART, event)
            }


        }

}