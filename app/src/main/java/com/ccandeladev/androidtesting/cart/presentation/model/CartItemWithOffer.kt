package com.ccandeladev.androidtesting.cart.presentation.model

import com.ccandeladev.androidtesting.cart.domain.model.CartItem
import com.ccandeladev.androidtesting.productlist.domain.model.Product

data class CartItemWithOffer(
    val cartItem: CartItem,
    val product: Product
)
