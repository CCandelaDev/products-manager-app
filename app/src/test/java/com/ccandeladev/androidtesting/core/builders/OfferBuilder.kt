package com.ccandeladev.androidtesting.core.builders

import com.ccandeladev.androidtesting.productlist.domain.model.Offer
import com.ccandeladev.androidtesting.productlist.domain.model.OfferType
import java.time.Instant

class OfferBuilder {

    private var id: String = "promotion-1"
    private var type: OfferType = OfferType.PERCENT
    private var productIds: List<String> = listOf("product-1")
    private var value: Double = 10.0
    private var buyQuantity: Int? = null
    private var startTime: Instant = Instant.now().minusSeconds(3600) //Now - 1 hour
    private var endTime: Instant = Instant.now().plusSeconds(3600)  //Now + 1 hour


    //With methods
    fun withId(id: String) = apply { this.id = id }
    fun withType(type: OfferType) = apply { this.type = type }
    fun withProductIds(ids: List<String>) = apply { this.productIds = ids }
    fun withBuyQuantity(buyQuantity: Int?) = apply { this.buyQuantity = buyQuantity }
    fun withValue(value: Double) = apply { this.value = value }
    fun withTimes(start: Instant, end: Instant) = apply {
        this.startTime = start
        this.endTime = end
    }

    //Build function
    fun build(): Offer {
        return Offer(
            id = id,
            type = type,
            productIds = productIds,
            value = value,
            buyQuantity = buyQuantity,
            startTime = startTime,
            endTime = endTime
        )
    }

}

fun offer(block: OfferBuilder.() -> Unit = {}) = OfferBuilder().apply (block).build()


