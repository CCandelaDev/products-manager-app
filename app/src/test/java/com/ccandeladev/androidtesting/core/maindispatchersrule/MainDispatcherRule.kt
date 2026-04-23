package com.ccandeladev.androidtesting.core.maindispatchersrule

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    // To create a fake coroutine for testing
    val scheduler: TestCoroutineScheduler = TestCoroutineScheduler(),
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(scheduler = scheduler)
): TestWatcher() {

    override fun starting(description: Description?) {
        Dispatchers.setMain(testDispatcher) //Fake thread
    }

    override fun finished(description: Description?) {
        Dispatchers.resetMain()
    }


}