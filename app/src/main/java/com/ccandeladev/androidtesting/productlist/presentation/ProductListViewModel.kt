package com.ccandeladev.androidtesting.productlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ccandeladev.androidtesting.productlist.domain.model.Product
import com.ccandeladev.androidtesting.productlist.domain.model.SortOption
import com.ccandeladev.androidtesting.productlist.domain.usecase.GetInventoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(private val getInventoryUseCase: GetInventoryUseCase) :
    ViewModel(
    ) {

    private val _uiState = MutableStateFlow<ProductListUiState>(value = ProductListUiState.Loading)
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    // SharedFlow  because is an ephemeral event
    private val _events = MutableSharedFlow<ProductListEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ProductListEvent> = _events // State for events

    // for the status of the filters
    private val _filtersVisible = MutableStateFlow<Boolean>(value = true)
    val filtersVisible: StateFlow<Boolean> = _filtersVisible.asStateFlow()

    // Begin loading the products
    init {
        loadProducts()
    }

    fun loadProducts() {
        _uiState.value = ProductListUiState.Loading
        getInventoryUseCase()
            .onEach { inventory: List<Product> ->
                val categories = inventory.map { it.category }.distinct().sorted()
                _uiState.value =
                    ProductListUiState.Success(
                        inventory = inventory,
                        categories = categories,
                        selectedCategory = null,// Waiting to complete
                        sortOption = SortOption.NONE //Waiting to complete (set one as default)
                    )
            }
            .catch { e: Throwable ->
                _uiState.value = ProductListUiState.Error(e.message.orEmpty())
            }
            .launchIn(viewModelScope)
    }

    fun setCategory(category: String?) {
        viewModelScope.launch {
            //Llamar settingRepository
            TODO()
        }
    }

    fun setSortOption(sortOption: SortOption) {
        //Llamar settingRepository
        TODO()
    }

    fun setFilterVisible(showFilter: Boolean) {
        _filtersVisible.value = showFilter
    }


}