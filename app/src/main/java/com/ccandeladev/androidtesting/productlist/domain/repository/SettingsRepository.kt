package com.ccandeladev.androidtesting.productlist.domain.repository

import com.ccandeladev.androidtesting.core.domain.model.ThemeMode
import com.ccandeladev.androidtesting.productlist.domain.model.SortOption
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    val inStockOnly: Flow<Boolean>
    suspend fun setInStockOnly(value: Boolean)


    val themeMode: Flow<ThemeMode>
    suspend fun setThemeMode(value: ThemeMode)


    val selectCategory: Flow<String?>
    suspend fun setSelectCategory(value: String?)


    val filtersVisible: Flow<Boolean>
    suspend fun setFiltersVisible(value: Boolean)


    val sortOption: Flow<SortOption>
    suspend fun setSortOption(value: SortOption)


}