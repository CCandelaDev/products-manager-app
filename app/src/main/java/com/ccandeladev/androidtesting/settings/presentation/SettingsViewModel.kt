package com.ccandeladev.androidtesting.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ccandeladev.androidtesting.core.domain.model.ThemeMode
import com.ccandeladev.androidtesting.productlist.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

//    private val _uiState = MutableStateFlow<SettingsUiState>(value = SettingsUiState())
//    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    val uiState : StateFlow<SettingsUiState> = combine(
        settingsRepository.inStockOnly,
        settingsRepository.themeMode
    ){inStockOnly, themeMode ->
        SettingsUiState(inStockOnly, themeMode)

    }.stateIn( //Better for testing
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), //Only if someone observer it
        initialValue = SettingsUiState()
    )

    // Don't correct for testing
//    init {
//        loadSettings()
//    }
//
//    private fun loadSettings() {
////        combine(
////            settingsRepository.inStockOnly,
////            settingsRepository.themeMode
////        ) { inStockOnly, themeMode ->
////            _uiState.value = SettingsUiState(inStockOnly = inStockOnly, themeMode = themeMode)
////
////        }.launchIn(viewModelScope)
//
//        viewModelScope.launch {
//            delay(2000)
//            combine(
//                settingsRepository.inStockOnly,
//                settingsRepository.themeMode
//            ) { inStockOnly, themeMode ->
//                _uiState.value = SettingsUiState(inStockOnly = inStockOnly, themeMode = themeMode)
//
//            }.launchIn(this)
//        }
//    }


    fun setInStockOnly(newState: Boolean) {
        viewModelScope.launch {
            settingsRepository.setInStockOnly(newState)
        }
    }

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(themeMode
            )
        }
    }

}