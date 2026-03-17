package com.ccandeladev.androidtesting.productlist.presentation

// Separate message from the state
sealed interface ProductListEvent {
    data class ShowMessage(val message: String): ProductListEvent
}