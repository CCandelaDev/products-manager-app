package com.ccandeladev.androidtesting.detail.presentation

import com.ccandeladev.androidtesting.productlist.domain.model.ProductWithOffer

sealed interface ProductDetailUiState {

    data object Loading: ProductDetailUiState
    data class Success(val item: ProductWithOffer?): ProductDetailUiState
    data class Error(val message: String): ProductDetailUiState
}


