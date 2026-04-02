package com.ccandeladev.androidtesting.cart.domain.usecase

import com.ccandeladev.androidtesting.cart.domain.model.CartItem
import com.ccandeladev.androidtesting.cart.domain.model.CartSummary
import com.ccandeladev.androidtesting.cart.domain.repository.CartRepository
import com.ccandeladev.androidtesting.productlist.domain.model.Offer
import com.ccandeladev.androidtesting.productlist.domain.model.Product
import com.ccandeladev.androidtesting.productlist.domain.model.ProductOffer
import com.ccandeladev.androidtesting.productlist.domain.repository.OfferRepository
import com.ccandeladev.androidtesting.productlist.domain.repository.ProductRepository
import com.ccandeladev.androidtesting.productlist.domain.usecase.GetOfferForProduct
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.time.Instant
import javax.inject.Inject

// Calculate the total purchase
class GetCartSummaryUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val offerRepository: OfferRepository,
    private val getOfferForProduct: GetOfferForProduct
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<CartSummary> {
        // Step 1: Observe changes in the shopping cart
        return cartRepository.getCartItems()
            // flatMapLatest cancels previous calculation when new cart items arrive
            .flatMapLatest { cartItems ->
                // Step 2: Extract unique product IDs from cart items
                val ids =
                    cartItems.mapTo(mutableSetOf()) { it.productId }
                // Step 3: Handle empty cart scenario
                if (ids.isEmpty()) {
                    flowOf(CartSummary(0.0, 0.0, 0.0))
                } else {
                    // Step 4: Combine two asynchronous data sources
                    combine(
                        // Source 1: Get product details (prices, etc.) for all product IDs
                        productRepository.getInventoryByIds(ids),
                        // Source 2: Get all available offers
                        offerRepository.getActiveOffers()
                    ) { products, offers ->
                        // Step 5: Calculate final summary when both data sources emit
                        calculateSummary(cartItems, products, offers)
                    }
                }
            }


    }

    /**
     * Calculates the complete cart summary including subtotal, discounts, and final total
     * @param cartItems List of products and quantities in the cart
     * @param products List of product details (price, name, etc.)
     * @param offers List of all available offers
     * @return CartSummary containing subtotal, discount total, and final total
     */
    private fun calculateSummary(
        cartItems: List<CartItem>,
        products: List<Product>,
        offers: List<Offer>
    ): CartSummary {
        // Step 1: Get current time to filter active offers only
        val now: Instant = Instant.now()

        // Step 2: Filter offers that are currently active (within start and end time)
        val activeOffers = offers.filter { it.startTime <= now && it.endTime >= now }

        // Step 3: Create a map for O(1) product lookup by ID
        val productsById = products.associateBy { it.id }

        // Step 4: Initialize accumulators
        var subtotal = 0.0
        var discountTotal = 0.0

        // Step 5: Iterate through each item in the cart
        for (cartItem in cartItems) {
            // Get product details (skip if product not found)
            val product = productsById[cartItem.productId] ?: continue

            // Calculate item's contribution to subtotal: price × quantity
            val itemTotal = product.price * cartItem.quantity // total of each product
            subtotal += itemTotal

            // Calculate and accumulate discount for this specific product
            discountTotal += calculateDiscountForProduct(
                product = product,
                quantity = cartItem.quantity,
                activeOffers = activeOffers
            )


        }
        // Step 6: Calculate final total (cannot be negative)
        val total = (subtotal - discountTotal).coerceAtLeast(0.0)

        // Step 7: Return the complete cart summary
        return CartSummary(subTotal = subtotal, discountTotal = discountTotal, finalTotal = total)
    }


    /**
     * Calculates discount amount for a specific product based on the best applicable offer
     * @param product The product being purchased
     * @param quantity How many units of the product are in cart
     * @param activeOffers List of currently active offers
     * @return Discount amount in currency units (not percentage)
     */
    private fun calculateDiscountForProduct(
        product: Product,
        quantity: Int,
        activeOffers: List<Offer>
    ): Double {

        // Step 1: Find the best applicable offer for this product
        val selectedOffer = getOfferForProduct(product, activeOffers)

        // Step 2: Apply discount logic based on offer type
        return when (selectedOffer) {
            // Case 1: "Buy X, Pay Y" offer (e.g., buy 3, pay 2 → 1 free)
            is ProductOffer.BuyXPayY -> {
                val buy = selectedOffer.buy // Number of items you need to buy to qualify
                val pay = selectedOffer.pay // Number of items you need to buy to qualify

                // Calculate how many items are free per group
                val freePerGroup = (buy - pay).coerceAtLeast(0)
                // Calculate how many complete groups fit in the total quantity
                val groups = quantity / buy
                // Total free items = free items per group × number of complete groups
                val freeItems = freePerGroup * groups
                // Discount = price of free items (no discount for incomplete groups)
                product.price * freeItems
            }

            // Case 2: Percentage discount offer (e.g., 20% off)
            is ProductOffer.Percent -> {
                val itemSubTotal = product.price * quantity // Calculate subtotal for this product
                itemSubTotal * (selectedOffer.percent / 100) // Discount = subtotal × percentage / 100
            }

            // Case 3: No applicable offer
            null -> 0.0
        }

    }


}