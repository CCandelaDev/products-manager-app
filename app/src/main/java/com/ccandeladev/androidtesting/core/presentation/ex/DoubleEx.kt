package com.ccandeladev.androidtesting.core.presentation.ex

import kotlin.math.roundToInt

fun Double.roundTo2Decimal(): Double {
    return (this * 100).roundToInt() / 100.0  //ensures 2 decimal places
}
