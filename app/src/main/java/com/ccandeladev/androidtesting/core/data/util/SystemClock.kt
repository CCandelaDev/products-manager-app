package com.ccandeladev.androidtesting.core.data.util

import com.ccandeladev.androidtesting.core.domain.util.Clock
import java.time.Instant
import javax.inject.Inject

class SystemClock @Inject constructor(): Clock {
    override fun now(): Instant {
        return Instant.now()
    }
}