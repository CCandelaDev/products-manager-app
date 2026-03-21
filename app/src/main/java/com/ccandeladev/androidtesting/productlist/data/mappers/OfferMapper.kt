package com.ccandeladev.androidtesting.productlist.data.mappers

import com.ccandeladev.androidtesting.productlist.data.local.database.entity.OfferEntity
import com.ccandeladev.androidtesting.productlist.data.remote.response.OfferResponse
import com.ccandeladev.androidtesting.productlist.domain.model.Offer
import com.ccandeladev.androidtesting.productlist.domain.model.OfferType
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.time.Instant

fun OfferEntity.toDomain(json: Json): Offer? { //Json to decode

    val decodeProductIds = runCatching {
        json.decodeFromString(
            ListSerializer(elementSerializer = String.serializer()),
            string = productIds
        )
    }.getOrNull() //For security

    val finalType = runCatching {
        OfferType.valueOf(
            type.trim().uppercase()
        )
    }.getOrNull()

    if (finalType == null || decodeProductIds == null) return null
    //finalType ?: return null (it's the same)

    val finalOfferValue = when(finalType){
        OfferType.PERCENT -> percent
        OfferType.BUY_X_APY_Y -> payY
    }?.toDouble()

    return Offer(
        id = id,
        productIds = decodeProductIds,
        type = finalType,
        value = finalOfferValue,
        buyQuantity = buyX,
        startTime = Instant.ofEpochSecond(startAtEpoch),
        endTime = Instant.ofEpochSecond(endAtEpoch)
    )
}


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

