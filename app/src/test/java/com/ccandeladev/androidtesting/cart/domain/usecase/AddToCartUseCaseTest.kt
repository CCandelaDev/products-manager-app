package com.ccandeladev.androidtesting.cart.domain.usecase

import com.ccandeladev.androidtesting.core.domain.model.AppError
import com.ccandeladev.androidtesting.core.fakes.FakeCartItemRepository
import com.ccandeladev.androidtesting.core.fakes.FakeProductRepository
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AddToCartUseCaseTest {

    @Test
    fun zero_quantity_throws_QuantityMustBePositive() = runTest { // Because is Coroutine
        //Given
        val fakeCartRepository = FakeCartItemRepository()
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
        val fakeCartRepository = FakeCartItemRepository()
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
        val fakeCartRepository = FakeCartItemRepository()
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

}