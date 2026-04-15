package com.ccandeladev.androidtesting.cart.domain.usecase

import com.ccandeladev.androidtesting.cart.domain.repository.CartRepository
import com.ccandeladev.androidtesting.core.builders.product
import com.ccandeladev.androidtesting.core.domain.model.AppError
import com.ccandeladev.androidtesting.core.fakes.FakeCartRepository
import com.ccandeladev.androidtesting.core.fakes.FakeProductRepository
import com.ccandeladev.androidtesting.productlist.domain.repository.ProductRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AddToCartUseCaseTest {

    @Test
    fun zero_quantity_throws_QuantityMustBePositive() = runTest { // Because is Coroutine
        //Given
        val fakeCartRepository = FakeCartRepository()
        val fakeProductRepository = FakeProductRepository().apply {
            setInventory(emptyList())
        }

        val useCase = AddToCartUseCase(
            fakeCartRepository,
            fakeProductRepository
        )

        //When
        //To avoid try-catch --> runCatching
        val exception = runCatching { useCase("id", 0) }.exceptionOrNull()

        //Then
        assertTrue(exception is AppError.Validation.QuantityMustBePositive)
        //assert(exception is AppError.Validation.QuantityMustBePositive)
    }


    @Test
    fun negative_quantity_throws_QuantityMustBePositive() = runTest { // Because is Coroutine
        //Given
        val fakeCartRepository = FakeCartRepository()
        val fakeProductRepository = FakeProductRepository()

        val useCase = AddToCartUseCase(
            fakeCartRepository,
            fakeProductRepository
        )

        //When
        val exception = runCatching { useCase("id", -2) }.exceptionOrNull()

        //Then
        assertTrue(exception is AppError.Validation.QuantityMustBePositive)
        //assert(exception is AppError.Validation.QuantityMustBePositive)
    }


    @Test
    fun non_existing_products_throws_NotFoundError() = runTest { // Because is Coroutine
        //Given
        val fakeCartRepository = FakeCartRepository()
        val fakeProductRepository = FakeProductRepository()

        val useCase = AddToCartUseCase(
            fakeCartRepository,
            fakeProductRepository
        )

        //When
        val exception = runCatching { useCase("id", 1) }.exceptionOrNull()

        //Then
        assertTrue(exception is AppError.NotFoundError)
        //assert(exception is AppError.Validation.QuantityMustBePositive)
    }

    @Test
    //Verify stock management
    fun insufficient_stock_throws_InsufficientStock() = runTest {

        //Given
        val productId = "product-1"
        val fakeCartRepository = FakeCartRepository()
        //We want to test the stock management, so we need to have a product with a specific stock
        val fakeProductRepository = FakeProductRepository().apply {
            setInventory(
                listOf(
                    product {
                        withId(productId)
                        withStock(2)
                    }
                ))
        }
        val useCase = AddToCartUseCase(
            fakeCartRepository,
            fakeProductRepository
        )

        //When
        val exception =
            runCatching { useCase(productId = productId, quantity = 5) }.exceptionOrNull()

        //Then
        assertTrue(exception is AppError.Validation.InsufficientStock)
        assertEquals(2, (exception as AppError.Validation.InsufficientStock).available)
    }

    // To check products add to cart
    @Test
    fun successful_case_adds_it_to_cart() = runTest {

        //Given
        val productId = "product-1"
        val fakeCartRepository = FakeCartRepository()
        //We want to test the stock management, so we need to have a product with a specific stock
        val fakeProductRepository = FakeProductRepository().apply {
            setInventory(
                listOf(
                    product {
                        withId(productId)
                        withStock(10)
                    }
                ))
        }
        val useCase = AddToCartUseCase(
            fakeCartRepository,
            fakeProductRepository
        )

        //when
        useCase(productId = productId, quantity = 3)

    }


    // To check products add to cart
    @Test
    fun successful_case_adds_items_to_cart() = runTest {
        //Given
        val productId = "product-1"
        val fakeCartRepository = FakeCartRepository()
        //We want to test the stock management, so we need to have a product with a specific stock
        val fakeProductRepository = FakeProductRepository().apply {
            setInventory(
                listOf(
                    product {
                        withId(productId)
                        withStock(10)
                    }
                ))
        }
        val useCase = AddToCartUseCase(
            fakeCartRepository,
            fakeProductRepository
        )

        //when
        useCase(productId, 3)

        //Then
        val items = fakeCartRepository.getCartItems().first()
        assertEquals(productId, items.first().productId)
        assertEquals(1, items.size)
        assertEquals(3, items.first().quantity)

    }

    @Test
    fun default_quantity_adds_one_item_to_cart() = runTest {
        //Given
        val productId = "product-1"
        val fakeCartRepository = FakeCartRepository()
        //We want to test the stock management, so we need to have a product with a specific stock
        val fakeProductRepository = FakeProductRepository().apply {
            setInventory(
                listOf(
                    product {
                        withId(productId)
                        withStock(10)
                    }
                ))
        }
        val useCase = AddToCartUseCase(
            fakeCartRepository,
            fakeProductRepository
        )
        //when
        useCase(productId) // Default quantity is 1

        //Then
        val items = fakeCartRepository.getCartItems().first()
        assertEquals(1, items.size)
        assertEquals(1, items.first().quantity)
    }


    // Mock: To check that the repository is not called when the quantity is zero or negative,
    // we can create a fake repository that keeps track of whether its methods were called.
    // Then, we can assert that the methods were not called when we invoke the use case with zero or negative quantity.
    @Test
    fun zero_quantity_does_not_call_any_repository() = runTest {
        //Given
        val productRepository = mockk<ProductRepository>()
        val cartRepository = mockk<CartRepository>()
        val useCase =
            AddToCartUseCase(productRepository = productRepository, cartRepository = cartRepository)

        //When
        val exception = runCatching { useCase(productId = "id", quantity = 0) }.exceptionOrNull()

        //Then  //To verify method is not called
        coVerify(exactly = 0) { productRepository.getProductById(any()) }
        coVerify(exactly = 0) { cartRepository.getCartItemById(any()) }
        coVerify(exactly = 0) { cartRepository.addToCart(any(), any()) }

    }


    @Test
    fun valid_product_calls_addToCart_with_expected_values() = runTest {
        //Given
        val productRepository = mockk<ProductRepository>()
        val cartRepository = mockk<CartRepository>()

        val product = product {
            withId("custom-id")
            withStock(10)
        }

        coEvery { productRepository.getProductById("custom-id") } returns flowOf(product)
        coEvery { cartRepository.getCartItemById("custom-id") } returns null
        coEvery { cartRepository.addToCart("custom-id", 3) } just Runs


        val useCase = AddToCartUseCase(cartRepository, productRepository)

        //When
        useCase("custom-id", 3)

        //Then (To verify that all flow is complete)
        coVerify(exactly = 1) { productRepository.getProductById("custom-id") }
        coVerify(exactly = 1) { cartRepository.getCartItemById("custom-id") }
        coVerify(exactly = 1) { cartRepository.addToCart("custom-id", 3) }
    }

}