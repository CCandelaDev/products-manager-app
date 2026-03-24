package com.ccandeladev.androidtesting.productlist.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ccandeladev.androidtesting.core.domain.model.ThemeMode
import com.ccandeladev.androidtesting.productlist.domain.model.SortOption
import com.ccandeladev.androidtesting.productlist.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    companion object {
        private val IN_STOCK_OPTION_KEY = booleanPreferencesKey(name = "IN_STOCK_OPTION_KEY")
        private val FILTERS_VISIBLE_KEY = booleanPreferencesKey(name = "FILTERS_VISIBLE_KEY")
        private val SELECTED_CATEGORY_KEY = stringPreferencesKey(name = "SELECTED_CATEGORY_KEY")
        private val THEME_MODE_KEY = intPreferencesKey(name = "THEME_MODE_KEY")
        private val SHORT_OPTION_KEY = stringPreferencesKey(name = "SHORT_OPTION_KEY")
    }


    // to avoid I/O failures
    private val dataStoreFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }

        }


    override val inStockOnly: Flow<Boolean> =
        dataStoreFlow.map { preferences -> preferences[IN_STOCK_OPTION_KEY] ?: false }

    override suspend fun setInStockOnly(value: Boolean) {
        dataStore.edit { preferences -> preferences[IN_STOCK_OPTION_KEY] = value }
    }


    override val themeMode: Flow<ThemeMode> = dataStoreFlow.map { preferences ->
        when (preferences[THEME_MODE_KEY]) {
            ThemeMode.SYSTEM.id -> ThemeMode.SYSTEM
            ThemeMode.LIGHT.id -> ThemeMode.LIGHT
            ThemeMode.DARK.id -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
    }


    override suspend fun setThemeMode(value: ThemeMode) {
        dataStore.edit { preferences ->
            when (value) {
                ThemeMode.SYSTEM -> preferences[THEME_MODE_KEY] = ThemeMode.SYSTEM.id
                ThemeMode.LIGHT -> preferences[THEME_MODE_KEY] = ThemeMode.LIGHT.id
                ThemeMode.DARK -> preferences[THEME_MODE_KEY] = ThemeMode.DARK.id
            }
        }
    }


    override val selectCategory: Flow<String?> =
        dataStoreFlow.map { preferences -> preferences[SELECTED_CATEGORY_KEY] }


    override suspend fun setSelectCategory(value: String?) {
        dataStore.edit { preferences ->
            if (value == null) {
                preferences.remove(SELECTED_CATEGORY_KEY)
            } else {
                preferences[SELECTED_CATEGORY_KEY] = value
            }
        }
    }


    override val filtersVisible: Flow<Boolean> =
        dataStoreFlow.map { preferences -> preferences[FILTERS_VISIBLE_KEY] ?: false }


    override suspend fun setFiltersVisible(value: Boolean) {
        dataStore.edit { preferences -> preferences[FILTERS_VISIBLE_KEY] = value }
    }


    override val sortOption: Flow<SortOption> = dataStoreFlow.map { preferences ->
        val raw = preferences[SHORT_OPTION_KEY]
        runCatching { SortOption.valueOf(raw ?: SortOption.NONE.name ) }.getOrDefault(SortOption.NONE)
    }


    override suspend fun setSortOption(value: SortOption) {
        dataStore.edit { preferences ->
            preferences[SHORT_OPTION_KEY] = value.name
        }
    }


}