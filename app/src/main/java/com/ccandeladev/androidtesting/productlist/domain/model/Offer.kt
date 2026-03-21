package com.ccandeladev.androidtesting.productlist.domain.model

import java.time.Instant

enum class PromotionType {
    PERCENT,
    BUY_X_APY_Y
}

data class Offer(
    val id: String,
    val type: PromotionType,
    val productList: List<String>,
    val value: Double, // for reuse to BUY_X_APY_Y
    val buyQuantity: Int? = null, // Quantity to get discount
    val startTime: Instant,
    val endTime: Instant //kotlin time
)
