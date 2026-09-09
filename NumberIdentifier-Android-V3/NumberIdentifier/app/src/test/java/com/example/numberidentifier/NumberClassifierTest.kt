package com.example.numberidentifier

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NumberClassifierTest {
    @Test
    fun zeroIsWholeIntegerRationalRealAndEven() {
        val r = NumberClassifier.classify(0)
        assertFalse(r.isNatural)
        assertTrue(r.isWhole)
        assertTrue(r.isInteger)
        assertTrue(r.isRational)
        assertTrue(r.isReal)
        assertFalse(r.isPrime)
        assertFalse(r.isComposite)
        assertTrue(r.parity == "Even")
    }

    @Test
    fun sevenIsNaturalWholeIntegerRationalRealAndPrime() {
        val r = NumberClassifier.classify(7)
        assertTrue(r.isNatural)
        assertTrue(r.isWhole)
        assertTrue(r.isInteger)
        assertTrue(r.isRational)
        assertTrue(r.isPrime)
        assertFalse(r.isComposite)
        assertTrue(r.parity == "Odd")
    }

    @Test
    fun negativeFiveIsIntegerRationalRealAndNegative() {
        val r = NumberClassifier.classify(-5)
        assertFalse(r.isNatural)
        assertFalse(r.isWhole)
        assertTrue(r.isInteger)
        assertTrue(r.isRational)
        assertTrue(r.isReal)
        assertTrue(r.sign == "Negative")
        assertTrue(r.parity == "Odd")
        assertFalse(r.isPrime)
        assertFalse(r.isComposite)
    }

    @Test
    fun fourIsCompositeAndEven() {
        val r = NumberClassifier.classify(4)
        assertTrue(r.isComposite)
        assertFalse(r.isPrime)
        assertTrue(r.parity == "Even")
    }
}
