package com.ccandeladev.androidtesting.productlist.domain.usecase

import com.ccandeladev.androidtesting.core.builders.offer
import com.ccandeladev.androidtesting.core.builders.product
import com.ccandeladev.androidtesting.productlist.domain.model.OfferType
import com.ccandeladev.androidtesting.productlist.domain.model.ProductOffer
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import org.junit.Test

class GetOfferForProductTest {

    private val useCase = GetOfferForProduct()

    @Test
    fun given_no_promotions_when_invoke_then_returns_null() {

        //Given
        val product = product()

        //When
        val response = useCase(product = product, emptyList())

        //Then
        assertNull(response)
    }

    @Test
    fun given_percent_promotion_when_invoke_then_returns_discounted_price_rounded_to_2_decimals() {

        //Given
        val product = product {
            withPrice(10.0)
            withId("product-id")
        }

        val offer =
            offer {
                withType(OfferType.PERCENT)
                withProductIds(listOf("product-id"))
                withValue(15.0)
            }

        //When
        val response = useCase(product = product, offers = listOf(offer))

        //Then
        assertTrue(response is ProductOffer.Percent) //to ensure is Percent (optional)
        response as ProductOffer.Percent
        assertEquals(8.50, response.discountedPrice, 0.001)
        assertEquals(15.0, response.percent, 0.001)


    }

    @Test
    fun given_buy_x_pay_y_and_percent_offer_when_invoke_prioritizes_buy_x_pay_y() {
        //Given
        val productId = "product-id"
        val product = product {
            withPrice(10.0)
            withId(productId)
        }

        val offerPercent =
            offer {
                withType(OfferType.PERCENT)
                withProductIds(listOf(productId))
                withValue(15.0)
            }

        val offerBuy = offer {
            withType(OfferType.BUY_X_PAY_Y)
            withProductIds(listOf(productId))
            withBuyQuantity(3) // quantity purchased
            withValue(2.0)  // what is paid
        }

        //When
        val response = useCase(product = product, offers = listOf(offerPercent, offerBuy))

        //Then
        assertTrue(response != null)
        when (response) {
            is ProductOffer.BuyXPayY -> {
                assertEquals("3x2", response.label)
                assertEquals(3, response.buy)
                assertEquals(2, response.pay)
            }

            is ProductOffer.Percent -> {
                fail("Expected BuyXPayY but got Percent")
            }

            null -> {
                fail("Response should not be null")
            }
        }
    }

    @Test
    fun given_multiple_percents_offers_then_invoke_returns_highest() {
        //Given
        val productId = "product-id"
        val product = product {
            withPrice(10.0)
            withId(productId)
        }

        val offerPercentOne =
            offer {
                withType(OfferType.PERCENT)
                withProductIds(listOf(productId))
                withValue(15.0)
            }
        val offerPercentTwo =
            offer {
                withType(OfferType.PERCENT)
                withProductIds(listOf(productId))
                withValue(50.0)
            }

        //When
        val response = useCase(product = product, offers = listOf(offerPercentOne, offerPercentTwo))

        //Then
        assertTrue(response != null)
        assertTrue(response is ProductOffer.Percent)
        assertEquals(50.0, (response as ProductOffer.Percent).percent, 0.001)
    }

    @Test
    fun given_buy_x_pay_y_without_buy_quantity_when_invoke_then_returns_null() {
        //Given
        val productId = "product-id"
        val product = product {
            withPrice(10.0)
            withId(productId)
        }
        val offerPercent =
            offer {
                withType(OfferType.PERCENT)
                withProductIds(listOf(productId))
                withValue(15.0)
            }

        val brokenOfferBuy =
            offer {
                withType(OfferType.BUY_X_PAY_Y)
                withProductIds(listOf(productId))
                withBuyQuantity(null)
                withValue(2.0)
            }

        //When
        val response = useCase(product, listOf(offerPercent, brokenOfferBuy))

        //Then
        assertNull(response)
    }

}