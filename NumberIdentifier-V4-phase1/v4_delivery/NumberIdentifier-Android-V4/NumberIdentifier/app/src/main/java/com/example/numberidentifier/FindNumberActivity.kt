package com.example.numberidentifier

import android.widget.LinearLayout

/** V4 feature 2 — square/cube/roots/tables logic arrives in a later phase. */
class FindNumberActivity : BaseActivity() {
    override fun screenTitle() = "Find the Number"
    override fun buildContent(container: LinearLayout) {
        container.addView(
            placeholderCard(
                "Coming next",
                "Find a number's square, square root, cube, and cube root, or generate its multiplication table."
            ),
            wrap()
        )
    }
}
