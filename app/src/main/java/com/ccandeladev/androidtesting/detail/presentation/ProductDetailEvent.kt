package com.ccandeladev.androidtesting.detail.presentation

sealed interface ProductDetailEvent {

    data object UNKNOWN_ERROR : ProductDetailEvent
    data object NETWORK_ERROR : ProductDetailEvent
    data object INSUFICIENT_STOCK_ERROR : ProductDetailEvent
    data object SUCCESS_ADD_TO_CART : ProductDetailEvent
}