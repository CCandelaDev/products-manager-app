package com.ccandeladev.androidtesting.productlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ccandeladev.androidtesting.productlist.domain.model.ProductOffer
import com.ccandeladev.androidtesting.productlist.domain.model.ProductWithOffer
import com.ccandeladev.androidtesting.productlist.domain.model.SortOption
import com.ccandeladev.androidtesting.productlist.domain.repository.SettingsRepository
import com.ccandeladev.androidtesting.productlist.domain.usecase.GetInventoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    getInventoryUseCase: GetInventoryUseCase,
    private val settingsRepository: SettingsRepository
) :
    ViewModel(
    ) {

    //    private val _uiState = MutableStateFlow<ProductListUiState>(value = ProductListUiState.Loading)
    //    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()
    val uiState: StateFlow<ProductListUiState> = combine(
        getInventoryUseCase(),
        settingsRepository.selectCategory,
        settingsRepository.sortOption
    ) { inventory, category, sortOption ->
        var filteredInventory = inventory

        if (category != null) { // user select category
            filteredInventory = filteredInventory.filter {
                it.product.category == category
            }
        }

        val sorted = when (sortOption) {
            SortOption.PRICE_ASC -> filteredInventory.sortedBy { effectivePrice(item = it) }
            SortOption.PRICE_DES -> filteredInventory.sortedByDescending { effectivePrice(item = it) }
            SortOption.NONE -> filteredInventory
            SortOption.DISCOUNT ->
                filteredInventory.sortedWith(
                    compareByDescending<ProductWithOffer> {
                        effectiveDiscountPercent(item = it)
                    }.thenBy { it.product.price }
                )
        }

        val categories = inventory.map { it.product.category }.distinct().sorted()

        ProductListUiState.Success(
            inventory = sorted,
            categories = categories,
            selectedCategory = category,
            sortOption = sortOption
        ) as ProductListUiState


    }.catch { e: Throwable ->
        emit(ProductListUiState.Error(e.message.orEmpty()))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProductListUiState.Loading
    )

    // SharedFlow  because is an ephemeral event
    private val _events = MutableSharedFlow<ProductListEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ProductListEvent> = _events // State for events

    val filterVisible: StateFlow<Boolean> = settingsRepository.filtersVisible.stateIn(
        scope = viewModelScope,
        initialValue = false,
        started = SharingStarted.WhileSubscribed(5000)

    )


    fun setCategory(category: String?) {
        viewModelScope.launch {
            //call settingRepository
            settingsRepository.setSelectCategory(category)
        }
    }

    fun setSortOption(sortOption: SortOption) {
        //call settingRepository
        viewModelScope.launch {
            settingsRepository.setSortOption(sortOption)
        }

    }

    fun setFilterVisible(showFilter: Boolean) {
        //_filtersVisible.value = showFilter
        viewModelScope.launch {
            settingsRepository.setFiltersVisible(showFilter)
        }
    }


    //
    private fun effectiveDiscountPercent(item: ProductWithOffer): Double {
        return when (val offer = item.offer) {
            is ProductOffer.Percent -> offer.percent
            else -> 0.0
        }
    }

    //
    private fun effectivePrice(item: ProductWithOffer): Double {
        return when (val offer = item.offer) {
            is ProductOffer.Percent -> offer.discountedPrice
            else -> item.product.price
        }
    }


}