package com.ccandeladev.androidtesting.core.domain.util

import java.time.Instant

interface Clock {
    fun now(): Instant
}