package com.ccandeladev.androidtesting.core.presentation.ex

import org.junit.Assert.assertEquals
import org.junit.Test

class DoubleExTest {

    @Test
    fun roundToDecimals_roundsCorrectly(){

        assertEquals(4.66,  4.6578.roundTo2Decimal(), 0.0)
        assertEquals(4.65,  4.6478.roundTo2Decimal(), 0.0)


    }

}