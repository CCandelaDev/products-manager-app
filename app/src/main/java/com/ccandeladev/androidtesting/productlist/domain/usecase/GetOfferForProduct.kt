package com.ccandeladev.androidtesting.productlist.domain.usecase

import com.ccandeladev.androidtesting.core.presentation.ex.roundTo2Decimal
import com.ccandeladev.androidtesting.productlist.domain.model.Offer
import com.ccandeladev.androidtesting.productlist.domain.model.OfferType
import com.ccandeladev.androidtesting.productlist.domain.model.Product
import com.ccandeladev.androidtesting.productlist.domain.model.ProductOffer
import javax.inject.Inject

class GetOfferForProduct @Inject constructor() {

    operator fun invoke(product: Product, offers: List<Offer>): ProductOffer? {
        val productOffers = offers.filter { it.productIds.contains(product.id) }

        val buyPayOffer = productOffers.firstOrNull() { it.type == OfferType.BUY_X_PAY_Y }
        //Improvement: Get the Best Buy_X_APY_Y promotion -> get ratio

        // we prioritize buyPayOffer
        if (buyPayOffer != null) {
            val buy = buyPayOffer.buyQuantity ?: return null
            val pay = buyPayOffer.value.toInt().coerceIn(0, buy)

            return ProductOffer.BuyXPayY(
                buy = buy,
                pay = pay,
                label = "${buy}x${pay}"
            )
        }

        val percentOffer =
            productOffers.filter { it.type == OfferType.PERCENT }
                .maxByOrNull { it.value }


        if (percentOffer != null) {
            val percent = percentOffer.value.coerceIn(0.0, 100.0)// discount min=0, max=100%
            val discountedPrice = (product.price * (1 - percent / 100.0)).roundTo2Decimal()

            return ProductOffer.Percent(percent = percent, discountedPrice = discountedPrice)
        }

        return null

    }

}