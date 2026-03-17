package com.ccandeladev.androidtesting.productlist.presentation

// Class for all the states
sealed class ProductListUiState {
    data object Loading: ProductListUiState()
    data class Error(val message: String): ProductListUiState()
    data class Success(
//      productList: List<>,
//      categories: List<>,
        val selectedCategory: String,
//      sortOption
    ): ProductListUiState()
}