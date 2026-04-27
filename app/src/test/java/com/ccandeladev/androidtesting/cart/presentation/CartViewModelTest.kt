package com.ccandeladev.androidtesting.cart.presentation

import app.cash.turbine.test
import com.ccandeladev.androidtesting.cart.domain.repository.CartRepository
import com.ccandeladev.androidtesting.cart.domain.usecase.GetCartItemsWithOffersUseCase
import com.ccandeladev.androidtesting.cart.domain.usecase.GetCartSummaryUseCase
import com.ccandeladev.androidtesting.cart.domain.usecase.UpdateCartItemUseCase
import com.ccandeladev.androidtesting.core.builders.DEFAULT_PRODUCT_ID
import com.ccandeladev.androidtesting.core.builders.cartItem
import com.ccandeladev.androidtesting.core.builders.product
import com.ccandeladev.androidtesting.core.domain.util.Clock
import com.ccandeladev.androidtesting.core.fakes.FakeCartRepository
import com.ccandeladev.androidtesting.core.fakes.FakeOfferRepository
import com.ccandeladev.androidtesting.core.fakes.FakeProductRepository
import com.ccandeladev.androidtesting.core.fakes.FakeSystemClock
import com.ccandeladev.androidtesting.core.maindispatchersrule.MainDispatcherRule
import com.ccandeladev.androidtesting.productlist.domain.repository.OfferRepository
import com.ccandeladev.androidtesting.productlist.domain.repository.ProductRepository
import com.ccandeladev.androidtesting.productlist.domain.usecase.GetOfferForProduct
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class CartViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun createViewmodel(
        productRepository: ProductRepository = FakeProductRepository(),
        cartRepository: CartRepository = FakeCartRepository(),
        offerRepository: OfferRepository = FakeOfferRepository(),
        clock: Clock = FakeSystemClock()

    ): CartViewModel {
        val getCartSummaryUseCase = GetCartSummaryUseCase(
            cartRepository = cartRepository,
            productRepository = productRepository,
            offerRepository = offerRepository,
            getOfferForProduct = GetOfferForProduct(),
            clock = clock
        )

        val updateCartItemUseCase = UpdateCartItemUseCase(
            cartRepository = cartRepository,
            productRepository = productRepository
        )

        val getCartItemsWithOffersUseCase = GetCartItemsWithOffersUseCase(
            cartRepository = cartRepository,
            productRepository = productRepository,
            offerRepository = offerRepository,
            getOfferForProduct = GetOfferForProduct(),
            clock = clock
        )
        return CartViewModel(
            cartRepository = cartRepository,
            getCartSummaryUseCase = getCartSummaryUseCase,
            updateCartItemUseCase = updateCartItemUseCase,
            getCartItemsWithOffersUseCase = getCartItemsWithOffersUseCase
        )
    }

    @Test
    fun `given cart data when emit then success state`() =
        runTest(mainDispatcherRule.scheduler) {
            //Given
            val p =
                product { withId(DEFAULT_PRODUCT_ID); withName("Foam Roller"); withPrice(100.0) }
            val item = cartItem { withQuantity(3) }

            val fakeProductRepository =
                FakeProductRepository().apply { setInventory(listOf(p)) }
            val fakeCartRepository = FakeCartRepository().apply { setCartItems(listOf(item)) }

            //When
            val viewModel = createViewmodel(
                productRepository = fakeProductRepository,
                cartRepository = fakeCartRepository
            )

            //Then
            viewModel.uiState.test {
                val state = awaitItem() as CartUiState.Success
                assertEquals(1, state.cartItems.size)
                assertEquals(300.0, state.summary?.finalTotal)
                cancelAndIgnoreRemainingEvents()

            }

        }

    @Test
    fun `given quantity one  when decreased quantity then removes items from cart`() =
        runTest(mainDispatcherRule.scheduler) {
            //Given
            val p =
                product { withId(DEFAULT_PRODUCT_ID); withStock(5); withPrice(100.0) }
            val item = cartItem { withQuantity(1) }
            val fakeProductRepository =
                FakeProductRepository().apply { setInventory(listOf(p)) }
            val fakeCartRepository = FakeCartRepository().apply { setCartItems(listOf(item)) }
            val viewModel = createViewmodel(fakeProductRepository, fakeCartRepository)


            viewModel.uiState.test {
                awaitItem()

                //When
                viewModel.decreaseQuantity(DEFAULT_PRODUCT_ID, 1)

                //Then
                val state = awaitItem() as CartUiState.Success
                assertTrue(state.cartItems.isEmpty())
                assertEquals(0.0, state.summary?.finalTotal)
                cancelAndIgnoreRemainingEvents()
            }

        }

    @Test
    fun `given insufficient stock  when update quantity then emits error event`() =
        runTest(mainDispatcherRule.scheduler) {

            //Given
            val p =
                product { withId(DEFAULT_PRODUCT_ID); withStock(2) }
            val item = cartItem { withProductId(DEFAULT_PRODUCT_ID); withQuantity(1) }
            val fakeProductRepository =
                FakeProductRepository().apply { setInventory(listOf(p)) }
            val fakeCartRepository = FakeCartRepository().apply { setCartItems(listOf(item)) }
            val viewModel = createViewmodel(fakeProductRepository, fakeCartRepository)

            viewModel.events.test {

                //When
                viewModel.increaseQuantity(DEFAULT_PRODUCT_ID, 5)

                //Then
                val event = awaitItem()
                assertTrue(event is CartEvent.ShowMessage)
                cancelAndConsumeRemainingEvents()
            }
        }

}



















