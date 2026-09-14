package com.example.numberidentifier

import android.widget.LinearLayout

/** V4 feature 7 — browsable number-facts reference chart arrives in a later phase. */
class InfoChartActivity : BaseActivity() {
    override fun screenTitle() = "Information Chart"
    override fun buildContent(container: LinearLayout) {
        container.addView(
            placeholderCard(
                "Coming next",
                "A browsable reference chart of number facts — e.g. properties for 1-100 at a glance, " +
                    "so you can look things up without typing a number in."
            ),
            wrap()
        )
    }
}
