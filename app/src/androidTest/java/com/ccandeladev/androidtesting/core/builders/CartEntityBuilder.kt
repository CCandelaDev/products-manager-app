package com.ccandeladev.androidtesting.core.builders

import com.ccandeladev.androidtesting.cart.data.local.database.entity.CartEntity

class CartEntityBuilder {
    private var productId: String = DEFAULT_PRODUCT_ID
    private var quantity: Int = 3


    fun withProductId(productId: String) = apply { this.productId = productId }
    fun withQuantity(quantity: Int) = apply { this.quantity = quantity }

    fun build(): CartEntity{
        return CartEntity(
            productId = productId,
            quantity = quantity
        )
    }

}

fun cartItemEntity(block: CartEntityBuilder.() -> Unit = {}): CartEntity {
    return CartEntityBuilder().apply(block = block).build()
}