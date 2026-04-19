package com.ccandeladev.androidtesting.core.fakes

import com.ccandeladev.androidtesting.core.domain.util.Clock
import java.time.Instant

//Interface Clock implementation
class FakeSystemClock(): Clock {
    private var currentTime: Instant = Instant.now()

    fun setTime(time: Instant) {
        currentTime = time
    }

    //Other functions
    fun advanceTime(seconds: Long){
        currentTime = currentTime.plusSeconds(seconds)
    }

    //Implemented
    override fun now(): Instant {
        return currentTime
    }
}