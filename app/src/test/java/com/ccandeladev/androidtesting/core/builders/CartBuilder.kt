package com.ccandeladev.androidtesting.core.builders

import com.ccandeladev.androidtesting.cart.domain.model.CartItem


class CartBuilder {

    private var productId: String = DEFAULT_PRODUCT_ID
    private var quantity: Int = 3


    fun withProductId(productId: String) = apply { this.productId = productId }
    fun withQuantity(quantity: Int) = apply { this.quantity = quantity }

    fun build(): CartItem{
        return CartItem(
            productId = productId,
            quantity = quantity
        )
    }

}

fun cartItem(block: CartBuilder.() -> Unit = {}): CartItem {
    return CartBuilder().apply(block = block).build()
}