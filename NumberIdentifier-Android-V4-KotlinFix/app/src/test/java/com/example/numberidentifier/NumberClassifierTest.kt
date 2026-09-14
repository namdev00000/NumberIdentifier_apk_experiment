package com.example.numberidentifier

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NumberClassifierTest {
    @Test
    fun sevenIsPrimeOddNaturalWholeInteger() {
        val r = NumberClassifier.classify("7")!!
        assertTrue(r.isNatural)
        assertTrue(r.isWhole)
        assertTrue(r.isInteger)
        assertTrue(r.isPrime == true)
        assertTrue(r.isOdd == true)
        assertEquals(listOf(1L, 7L), r.divisors)
    }

    @Test
    fun twelveIsCompositeEven() {
        val r = NumberClassifier.classify("12")!!
        assertTrue(r.isComposite == true)
        assertTrue(r.isEven == true)
        assertEquals(6, r.divisorCount)
    }

    @Test
    fun negativeFiveIsIntegerNotNatural() {
        val r = NumberClassifier.classify("-5")!!
        assertTrue(!r.isNatural)
        assertTrue(r.isInteger)
        assertTrue(r.isPrime == true)
    }
}
