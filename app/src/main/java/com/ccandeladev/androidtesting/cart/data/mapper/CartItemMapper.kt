package com.ccandeladev.androidtesting.cart.data.mapper

import com.ccandeladev.androidtesting.cart.data.local.database.entity.CartEntity
import com.ccandeladev.androidtesting.cart.domain.model.CartItem

fun CartEntity.toDomain(): CartItem {
    return CartItem(
        productId = productId,
        quantity = quantity
    )
}

fun CartItem.toEntity(): CartEntity {
    return CartEntity(
        productId = productId,
        quantity = quantity
    )
}