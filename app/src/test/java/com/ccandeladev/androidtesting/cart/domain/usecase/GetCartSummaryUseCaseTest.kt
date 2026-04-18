package com.ccandeladev.androidtesting.cart.domain.usecase

import com.ccandeladev.androidtesting.core.fakes.FakeCartRepository
import com.ccandeladev.androidtesting.core.fakes.FakeOfferRepository
import com.ccandeladev.androidtesting.core.fakes.FakeProductRepository
import com.ccandeladev.androidtesting.core.fakes.FakeSystemClock

class GetCartSummaryUseCaseTest {

    private lateinit var clock: FakeSystemClock
    private lateinit var cart: FakeCartRepository
    private lateinit var product: FakeProductRepository
    private lateinit var offer: FakeOfferRepository

}