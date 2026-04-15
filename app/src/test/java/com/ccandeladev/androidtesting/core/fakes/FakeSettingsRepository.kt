package com.ccandeladev.androidtesting.core.fakes

import com.ccandeladev.androidtesting.core.domain.model.ThemeMode
import com.ccandeladev.androidtesting.productlist.domain.model.SortOption
import com.ccandeladev.androidtesting.productlist.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSettingsRepository : SettingsRepository {

    // variable to modify instead of the actual Data Base
    private val _inStockOnly = MutableStateFlow(false)
    private val _themeMode = MutableStateFlow<ThemeMode>(ThemeMode.SYSTEM)
    private val _selectCategory = MutableStateFlow<String?>(null)
    private val _filtersVisible = MutableStateFlow(true)
    private val _sortOption = MutableStateFlow(SortOption.NONE)


    override val inStockOnly: Flow<Boolean> = _inStockOnly.asStateFlow()


    override suspend fun setInStockOnly(value: Boolean) {
        _inStockOnly.value = value
    }

    override val themeMode: Flow<ThemeMode> = _themeMode.asStateFlow()


    override suspend fun setThemeMode(value: ThemeMode) {
        _themeMode.value = value
    }

    override val selectCategory: Flow<String?> = _selectCategory.asStateFlow()


    override suspend fun setSelectCategory(value: String?) {
        _selectCategory.value = value
    }

    override val filtersVisible: Flow<Boolean> = _filtersVisible.asStateFlow()


    override suspend fun setFiltersVisible(value: Boolean) {
        _filtersVisible.value = value
    }

    override val sortOption: Flow<SortOption> = _sortOption.asStateFlow()


    override suspend fun setSortOption(value: SortOption) {
        _sortOption.value = value
    }
}