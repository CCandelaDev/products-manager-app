package com.ccandeladev.androidtesting.cart.domain.ex

import com.ccandeladev.androidtesting.productlist.domain.model.Offer
import java.time.Instant

fun List<Offer>.activeAt(now: Instant): List<Offer>{
     return this.filter {
        it.startTime <= now && it.endTime >= now
    }
}