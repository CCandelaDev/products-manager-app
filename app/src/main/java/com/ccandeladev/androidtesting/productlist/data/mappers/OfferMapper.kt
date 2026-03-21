package com.ccandeladev.androidtesting.productlist.data.mappers

import com.ccandeladev.androidtesting.productlist.data.local.database.entity.OfferEntity
import com.ccandeladev.androidtesting.productlist.data.remote.response.OfferResponse
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

// convert response to Entity
fun OfferResponse.toEntity(json: Json): OfferEntity? {

    //This way we don't save it if it's null (no offers)
    if (startAtEpoch == null || endAtEpoch == null) return null

    val productIds = listOf(productId)
    val productIdsJson: String = json.encodeToString(
        serializer = ListSerializer(elementSerializer = String.serializer()),
        value = productIds
    )

    return OfferEntity(
        id = id,
        productIds = productIdsJson,
        type = type,
        percent = percent,
        buyX = buyX,
        payY = buyY,
        startAtEpoch = startAtEpoch,
        endAtEpoch = endAtEpoch


    )

}