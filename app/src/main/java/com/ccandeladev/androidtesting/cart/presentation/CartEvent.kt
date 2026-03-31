package com.ccandeladev.androidtesting.cart.presentation

sealed interface CartEvent {

    data class ShowMessage(val message: String): CartEvent
}