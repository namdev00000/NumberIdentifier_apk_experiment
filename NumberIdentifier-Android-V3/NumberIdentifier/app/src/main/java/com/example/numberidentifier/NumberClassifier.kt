package com.example.numberidentifier

object NumberClassifier {
    data class Result(
        val isNatural: Boolean,
        val isWhole: Boolean,
        val isInteger: Boolean,
        val isRational: Boolean,
        val isIrrational: Boolean,
        val isReal: Boolean,
        val sign: String,
        val parity: String,
        val isPrime: Boolean,
        val isComposite: Boolean
    )

    /** Natural numbers use the convention {1, 2, 3, ...}. */
    fun classify(n: Long): Result {
        val prime = n >= 2 && isPrime(n)
        val composite = n >= 2 && !prime
        return Result(
            isNatural = n >= 1,
            isWhole = n >= 0,
            isInteger = true,
            isRational = true,
            isIrrational = false,
            isReal = true,
            sign = when {
                n > 0 -> "Positive"
                n < 0 -> "Negative"
                else -> "Zero"
            },
            parity = if (n % 2L == 0L) "Even" else "Odd",
            isPrime = prime,
            isComposite = composite
        )
    }

    private fun isPrime(n: Long): Boolean {
        if (n < 2) return false
        if (n == 2L) return true
        if (n % 2L == 0L) return false
        var divisor = 3L
        while (divisor <= n / divisor) {
            if (n % divisor == 0L) return false
            divisor += 2
        }
        return true
    }
}
