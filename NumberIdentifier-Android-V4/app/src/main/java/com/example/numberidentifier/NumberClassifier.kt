package com.example.numberidentifier

import kotlin.math.abs
import kotlin.math.sqrt

object NumberClassifier {
    data class Result(
        val raw: String,
        val isWhole: Boolean,
        val isNatural: Boolean,
        val isPrime: Boolean?,
        val isComposite: Boolean?,
        val isEven: Boolean?,
        val isOdd: Boolean?,
        val isRational: Boolean,
        val isIrrational: Boolean,
        val isInteger: Boolean,
        val isReal: Boolean,
        val isComplex: Boolean,
        val isPerfectSquare: Boolean?,
        val hasRealSquareRoot: Boolean,
        val isPerfectCube: Boolean?,
        val hasRealCubeRoot: Boolean,
        val divisorCount: Int?,
        val divisors: List<Long>,
        val multiples: List<String>,
        val sign: String
    )

    fun classify(raw: String): Result? {
        val text = raw.trim()
        if (text.isEmpty()) return null
        val value = text.toDoubleOrNull() ?: return null
        if (!value.isFinite()) return null

        val longValue = text.toLongOrNull()
        val isInteger = longValue != null
        val n = longValue ?: value.toLong()
        val positiveInteger = isInteger && n > 1
        val prime = if (positiveInteger) isPrime(n) else null
        val composite = if (positiveInteger) !isPrime(n) else null
        val even = if (isInteger) n % 2L == 0L else null
        val square = if (isInteger && n >= 0) isPerfectSquare(n) else null
        val cube = if (isInteger) isPerfectCube(n) else null
        val divisorData = if (isInteger && n != 0L) divisorsOf(abs(n)) else emptyList()
        val divisorCount = if (isInteger && n != 0L) divisorData.size else null
        val multiples = if (isInteger) (1..10).map { "${n} × $it = ${n * it}" } else emptyList()

        return Result(
            raw = text,
            isWhole = isInteger && n >= 0,
            isNatural = isInteger && n >= 1,
            isPrime = prime,
            isComposite = composite,
            isEven = even,
            isOdd = even?.not(),
            isRational = true,
            isIrrational = false,
            isInteger = isInteger,
            isReal = true,
            isComplex = true,
            isPerfectSquare = square,
            hasRealSquareRoot = value >= 0,
            isPerfectCube = cube,
            hasRealCubeRoot = true,
            divisorCount = divisorCount,
            divisors = divisorData,
            multiples = multiples,
            sign = when {
                value > 0 -> "Positive"
                value < 0 -> "Negative"
                else -> "Zero"
            }
        )
    }

    private fun isPrime(n: Long): Boolean {
        if (n < 2) return false
        if (n == 2L) return true
        if (n % 2L == 0L) return false
        var d = 3L
        while (d <= n / d) {
            if (n % d == 0L) return false
            d += 2
        }
        return true
    }

    private fun isPerfectSquare(n: Long): Boolean {
        val root = sqrt(n.toDouble()).toLong()
        return root * root == n || (root + 1) * (root + 1) == n
    }

    private fun isPerfectCube(n: Long): Boolean {
        val root = Math.cbrt(n.toDouble()).toLong()
        return root * root * root == n || (root + 1) * (root + 1) * (root + 1) == n || (root - 1) * (root - 1) * (root - 1) == n
    }

    private fun divisorsOf(n: Long): List<Long> {
        val result = mutableListOf<Long>()
        var d = 1L
        while (d <= n / d) {
            if (n % d == 0L) {
                result.add(d)
                if (d != n / d) result.add(n / d)
            }
            d++
        }
        return result.sorted()
    }
}
