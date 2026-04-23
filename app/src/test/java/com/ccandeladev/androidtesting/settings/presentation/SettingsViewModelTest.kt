package com.ccandeladev.androidtesting.settings.presentation

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.ccandeladev.androidtesting.core.domain.model.ThemeMode
import com.ccandeladev.androidtesting.core.fakes.FakeSettingsRepository
import com.ccandeladev.androidtesting.core.maindispatchersrule.MainDispatcherRule
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule() //To manage Android main thread


//    @Test
//    fun secondExample() = runTest(context = mainDispatcherRule.scheduler) {
//        // GIVEN: Setup repository with an initial state
//        val settingsRepository = FakeSettingsRepository().apply {
//            setInStockOnly(true)
//        }
//
//        // WHEN: Initialize the ViewModel
//        val viewModel = SettingsViewModel(settingsRepository = settingsRepository)
//
//        // START COLLECTION: Since 'uiState' uses 'WhileSubscribed', it needs at least one
//        // active collector to trigger the internal 'combine' logic and start emitting values.
//        val job = launch { viewModel.uiState.collect {} }
//
//        // SYNCHRONIZATION: Wait until all pending coroutines (like the 'stateIn' internal block)
//        // are executed before making assertions.
//        advanceUntilIdle()
//
//        // THEN: Verify the state has been updated correctly
//        assertTrue(viewModel.uiState.value.inStockOnly)
//
//        // CLEANUP: We must cancel the collection job. Since 'collect' on a StateFlow never ends,
//        // if we don't cancel it, the coroutine will leak and might prevent the test from finishing.
//        job.cancel()
//    }

    @Test
    fun `given repository with values when viewmodel is initialized then ui state is update`() =
        runTest(mainDispatcherRule.scheduler) {

            //Given: Initialize the ViewModel. It will internally start the 'stateIn' flow
            val settingsRepository: FakeSettingsRepository = FakeSettingsRepository().apply {
                setInStockOnly(true)
            }

            //When
            val viewModel = SettingsViewModel(settingsRepository = settingsRepository)

            //Then: with lib. turbine
            viewModel.uiState.test { //with turbine, immediate observation
                val state = awaitItem() // to stop test until the flow has something
                assertTrue(state.inStockOnly)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given viewmodel when theme mode is change then ui state and repository are update`() =
        runTest(mainDispatcherRule.scheduler) {
            //Given
            val settingsRepository: FakeSettingsRepository = FakeSettingsRepository()
            val viewModel = SettingsViewModel(settingsRepository)

            viewModel.uiState.test {
                awaitItem()

                //When
                viewModel.setThemeMode(ThemeMode.DARK)

                //Then
                val updateState = awaitItem()
                assertEquals(ThemeMode.DARK, updateState.themeMode)
                assertEquals(ThemeMode.DARK, settingsRepository.themeMode.first())
                cancelAndIgnoreRemainingEvents()
            }

        }


    @Test
    fun `given viewmodel when in stock only is change then ui state and repository are update`() =
        runTest(mainDispatcherRule.scheduler) {

            //Workaround
            turbineScope {
                val settingsRepository = FakeSettingsRepository()
                val viewModel = SettingsViewModel(settingsRepository)
                //testIn: start to listening the flow
                val state = viewModel.uiState.testIn(this)
                state.awaitItem() //first flow, initial value

                viewModel.setInStockOnly(true) //change value

                val updateState = state.awaitItem() // await for repository update
                assertEquals(true, updateState.inStockOnly)
                assertEquals(true, settingsRepository.inStockOnly.first())
                state.cancelAndIgnoreRemainingEvents() //To stop listening the flow
            }


        }


    @Test
    fun `given viewmodel when repository change externally then ui state update automatically`() =
        runTest(mainDispatcherRule.scheduler) {

            turbineScope {
                val settingsRepository: FakeSettingsRepository = FakeSettingsRepository()
                val viewModel = SettingsViewModel(settingsRepository = settingsRepository)

                val state = viewModel.uiState.testIn(this)
                state.awaitItem()

                // I do it externally, not with the viewmodel
                settingsRepository.setInStockOnly(true)

                val updateState = state.awaitItem()
                assertTrue(updateState.inStockOnly)
                state.cancelAndIgnoreRemainingEvents()
            }

        }
}





















