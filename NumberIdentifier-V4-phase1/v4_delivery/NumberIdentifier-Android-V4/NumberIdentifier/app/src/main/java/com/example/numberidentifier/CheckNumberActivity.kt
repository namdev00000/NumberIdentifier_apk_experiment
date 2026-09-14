package com.example.numberidentifier

import android.widget.LinearLayout

/** V4 feature 1 — full classification logic arrives in the next phase. */
class CheckNumberActivity : BaseActivity() {
    override fun screenTitle() = "Check the Number"
    override fun buildContent(container: LinearLayout) {
        container.addView(
            placeholderCard(
                "Coming next",
                "Enter a number to see every property at a glance — whole, natural, prime, composite, " +
                    "even/odd, rational/irrational, integer, complex, perfect square/cube — plus a second " +
                    "tool to list its divisors and multiples."
            ),
            wrap()
        )
    }
}
