package com.ccandeladev.androidtesting.detail.presentation

import com.ccandeladev.androidtesting.productlist.domain.model.ProductWithOffer

data class ProductDetailUiState (
    val item: ProductWithOffer? = null,
    val isLoading: Boolean = true
)
