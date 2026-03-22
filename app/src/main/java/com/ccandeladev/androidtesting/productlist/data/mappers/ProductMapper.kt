package com.ccandeladev.androidtesting.productlist.data.mappers

import com.ccandeladev.androidtesting.productlist.data.local.database.entity.ProductEntity
import com.ccandeladev.androidtesting.productlist.data.remote.response.ProductResponse
import com.ccandeladev.androidtesting.productlist.domain.model.Product

// ###mappers for ProductsRepository Impl###
//Convert what is in the internet into something the BD can save
fun ProductResponse.toEntity(): ProductEntity {
    val finalPrice = priceCents?.toDouble()?.div(other = 100) ?: 0.0

    return ProductEntity(
        id = id,
        name = name,
        description = description,
        price = finalPrice,
        category = category,
        stock = stock,
        imageUrl = imageUrl
    )
}

//Convert what is in the database into something the UI can render.
fun ProductEntity.toDomain(): Product? {

    if (category.isNullOrEmpty()) return null
    return Product(
        id = id,
        name = name,
        description = description.orEmpty(),
        price = price,
        category = category,
        stock = stock ?: 0,
        imageUrl = imageUrl
    )
}