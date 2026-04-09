package com.ccandeladev.androidtesting.core

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class CoroutineTestExample {

    private suspend fun coroutineSum(a:Int, b:Int): Int{
        delay(14500)
        return a + b
    }

    @Test
    fun coroutinesSum_returnsCorrectSum() = runTest{
        val result = coroutineSum(4, 4 )

        assertEquals(8, result)
    }
}