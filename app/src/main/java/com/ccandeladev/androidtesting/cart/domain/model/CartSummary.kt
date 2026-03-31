package com.ccandeladev.androidtesting.cart.domain.model

data class CartSummary(
    val subTotal: Double,
    val discountTotal: Double,
    val finalTotal: Double
)

