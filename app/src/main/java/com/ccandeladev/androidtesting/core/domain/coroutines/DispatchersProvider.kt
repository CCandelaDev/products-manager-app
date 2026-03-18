package com.ccandeladev.androidtesting.core.domain.coroutines

import kotlinx.coroutines.CoroutineDispatcher

// Use this coroutines dispatcher, best for testing
interface DispatchersProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
}