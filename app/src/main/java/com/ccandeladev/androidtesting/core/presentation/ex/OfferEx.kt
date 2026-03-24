package com.ccandeladev.androidtesting.core.presentation.ex

import com.ccandeladev.androidtesting.productlist.domain.model.Offer
import java.time.Instant

/**
 * Filter the offers that are valid at the given time
 */
fun List<Offer>.activeAt(now: Instant): List<Offer> {
    return this.filter { offer ->
        offer.startTime <= now && offer.endTime >= now
    }
}