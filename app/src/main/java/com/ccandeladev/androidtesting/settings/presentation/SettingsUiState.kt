package com.ccandeladev.androidtesting.settings.presentation

import com.ccandeladev.androidtesting.core.domain.model.ThemeMode

data class SettingsUiState(
    val inStockOnly: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)
