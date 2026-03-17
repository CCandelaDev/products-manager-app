package com.ccandeladev.androidtesting.productlist.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OffersResponse(
    @SerialName("offers")
    val offers: List<OffersResponse>
)

@Serializable
data class offerResponse(
    @SerialName("id")
    val id: String,
    @SerialName("productId")
    val productId: String,
    @SerialName("type")
    val type: String,
    @SerialName("percent")
    val percent: Int? = null,
    @SerialName("buyX")
    val buyX: Int? = null,
    @SerialName("buyY")
    val buyY: Int? = null,
    @SerialName("startAtEpoch")
    val startAtEpoch: Long? = null,
    @SerialName("endAtEpoch")
    val endAtEpoch: Long? = null,
)

