package com.ccandeladev.androidtesting

import app.cash.turbine.test
import com.ccandeladev.androidtesting.core.domain.model.ThemeMode
import com.ccandeladev.androidtesting.core.fakes.FakeSettingsRepository
import com.ccandeladev.androidtesting.core.maindispatchersrule.MainDispatcherRule
import com.ccandeladev.androidtesting.productlist.domain.repository.SettingsRepository
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun createViewModel(
        settingsRepository: SettingsRepository = FakeSettingsRepository()
    ) = MainViewModel(settingsRepository = settingsRepository)

    @Test
    fun `given theme mode when initialized then emits theme mode`() = runTest {

        //##########Con mocks#############
        //Given
        val mockSettingsRepository = mockk<SettingsRepository>()
        every { mockSettingsRepository.themeMode } returns flowOf(ThemeMode.DARK)

        //When
        val viewModelMock = MainViewModel(mockSettingsRepository)

        //Then
        viewModelMock.themeMode.test {
            val mode = awaitItem() //wait and get first emitted value
            assertEquals(ThemeMode.DARK, mode)
        }

        //##########Con fakes#############
        //Then
        val fakeSettingsRepository = FakeSettingsRepository().apply { setThemeMode(ThemeMode.DARK) }
        //When
        val viewModelFake = createViewModel(fakeSettingsRepository)
        //Then
        viewModelFake.themeMode.test {
            val mode = awaitItem() //wait and get first emitted value
            assertEquals(ThemeMode.DARK, mode)
        }
    }

    @Test
    fun `given default repository when initialized then emits system theme mode`() = runTest {
        val viewModel = createViewModel()

        viewModel.themeMode.test {
            val mode = awaitItem() //wait and get first emitted value
            assertEquals(ThemeMode.SYSTEM, mode)
        }
    }

}