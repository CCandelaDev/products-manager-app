package com.ccandeladev.androidtesting.productlist.presentation

import com.ccandeladev.androidtesting.productlist.domain.model.Product
import com.ccandeladev.androidtesting.productlist.domain.model.SortOption

// Class for all the states
sealed class ProductListUiState {
    data object Loading : ProductListUiState()
    data class Error(val message: String) : ProductListUiState()
    data class Success(
        val inventory: List<Product>,
        val categories: List<String>,
        //productList: List<>,
        val selectedCategory: String?,
        //productlist.model.SortOption
        val sortOption: SortOption
    ) : ProductListUiState()
}