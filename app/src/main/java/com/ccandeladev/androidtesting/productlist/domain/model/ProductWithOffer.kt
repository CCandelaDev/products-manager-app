package com.ccandeladev.androidtesting.productlist.domain.model

data class ProductWithOffer(
    val product: Product,
    val offer: ProductOffer? = null
)


sealed interface ProductOffer {
    data class Percent(
        val percent: Double,
        val discountedPrice: Double
    ): ProductOffer


    data class BuyXPayY(
        val buy: Int,
        val pay: Int,
        val label: String
    ): ProductOffer
}