package com.ccandeladev.androidtesting.cart.domain.usecase

import com.ccandeladev.androidtesting.core.builders.product
import com.ccandeladev.androidtesting.core.domain.model.AppError
import com.ccandeladev.androidtesting.core.fakes.FakeCartRepository
import com.ccandeladev.androidtesting.core.fakes.FakeProductRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateCartItemUseCaseTest {

    @Test
    fun quantity_less_than_zero_throws_QuantityMustBePositive() = runTest {
        //Given
        val fakeProductRepository = FakeProductRepository()
        val fakeCartRepository = FakeCartRepository()

        val useCase = UpdateCartItemUseCase(fakeCartRepository, fakeProductRepository)

        //When
        val exception =
            runCatching { useCase(productId = "product-id", quantity = -1) }.exceptionOrNull()

        //Then
        assertTrue(exception is AppError.Validation.QuantityMustBePositive)
    }

    @Test
    fun given_quantity_equal_to_zero_then_removeFromCart() = runTest {
        //Given
        val fakeProductRepository = FakeProductRepository()
        val fakeCartRepository = FakeCartRepository()

        val productId = "product-id"
        fakeCartRepository.addToCart(productId, 5) //products in the cart

        val useCase = UpdateCartItemUseCase(fakeCartRepository, fakeProductRepository)

        //When
        useCase(productId = productId, quantity = 0) //Update quantity to zero

        //Then: Cart must be empty
        val cartItems = fakeCartRepository.getCartItems().first()
        assertTrue("Empty cart when quantity is zero", cartItems.isEmpty())

    }

    @Test
    fun given_quantity_greater_than_stock_throws_InsufficientStock() = runTest {
        //Given
        val productId = "product-id"
        val fakeCartRepository = FakeCartRepository()
        val fakeProductRepository = FakeProductRepository().apply {
            setInventory(
                listOf(product {
                    withId(productId)
                    withStock(4)
                })

            )
        }

        val useCase = UpdateCartItemUseCase(fakeCartRepository, fakeProductRepository)

        //When
        val exception = runCatching { useCase(productId = productId, 5) }.exceptionOrNull()

        //Then: Both are valid
        //assertTrue(exception is AppError.Validation.InsufficientStock)
        assertEquals(4, (exception as AppError.Validation.InsufficientStock).available)


    }

    @Test
    fun given_missing_product_when_invoke_then_throws_NotFoundError() = runTest {
        //Given
        val productId = "product-id"
        val fakeCartRepository = FakeCartRepository()
        val fakeProductRepository = FakeProductRepository().apply { setInventory(emptyList()) }
        val useCase = UpdateCartItemUseCase(fakeCartRepository, fakeProductRepository)

        //When
        val exception = runCatching { useCase("fakeProduct", 3) }.exceptionOrNull()

        //Then
        assertTrue(exception is AppError.NotFoundError)
    }

    @Test
    fun given_valid_product_and_quantity_when_invoke_then_updates_cart_item() = runTest {
        //Given
        val productId = "product-id"
        val product = product {
            withId(productId)
            withStock(3)
        }

        val fakeProductRepository =
            FakeProductRepository().apply { setInventory(listOf(product)) }

        val fakeCartRepository = FakeCartRepository().apply { addToCart(productId, 1) }
        val useCase = UpdateCartItemUseCase(fakeCartRepository, fakeProductRepository)

        // When: Update to new valid quantity(ej: 2)
        useCase(productId = productId, quantity = 2)

        //Then
        val cartItems = fakeCartRepository.getCartItems().first() //The items I have
        assertEquals(1, cartItems.size) //To verify product is in the cart
        assertEquals(2, cartItems.first().quantity) //To verify new quantity is 2

    }

}