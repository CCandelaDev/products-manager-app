package com.ccandeladev.androidtesting.core.builders

import com.ccandeladev.androidtesting.productlist.data.local.database.entity.OfferEntity

const val DEFAULT_OFFER_ID = "offer-id"

class OfferEntityBuilder {
    private var id: String = DEFAULT_OFFER_ID
    // JSON representation of IDs;
    // Room requires a String to persist lists in a single column.
    // Triple quotes avoid escaping double quotes.
    private var productIds: String = """["productId1"]"""
    private var type: String = "PERCENT"
    private var percent: Int? = null
    private var buyX: Int? = null
    private var payY: Int? = null
    private var startAtEpoch: Long = 1700000000L
    private var endAtEpoch: Long = 1800000000L


    //With methods
    fun withId(id: String) = apply { this.id = id }
    fun withProductIds(ids: String) = apply {
        this.productIds = ids
    }
    fun withType(type: String) = apply { this.type = type }
    fun withPercent(percent: Int?) = apply { this.percent = percent }
    fun withBuyX(buyX: Int?) = apply { this.buyX = buyX }
    fun withPayY(payY: Int?) = apply { this.payY = payY }
    fun withStartAtEpoch(start: Long) = apply { this.startAtEpoch = start }
    fun withEndAtEpoch(end: Long) = apply { this.endAtEpoch = end }

    //Build function
    fun build(): OfferEntity {
        return OfferEntity(
            id = id,
            type = type,
            productIds = productIds,
            percent = percent,
            buyX = buyX,
            payY = payY,
            startAtEpoch = startAtEpoch,
            endAtEpoch = endAtEpoch
        )
    }

}

fun offerEntity(block: OfferEntityBuilder.() -> Unit = {}) = OfferEntityBuilder().apply (block).build()