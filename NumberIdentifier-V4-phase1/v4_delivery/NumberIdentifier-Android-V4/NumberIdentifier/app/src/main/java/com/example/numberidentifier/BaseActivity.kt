package com.example.numberidentifier

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

/**
 * Shared scaffolding for every V4 screen: consistent theme, a top bar with
 * back button + title + (optional) theme toggle / trailing action, an
 * optional floating action button, and rounded-corner view helpers.
 *
 * Subclasses just implement [screenTitle] and [buildContent].
 */
abstract class BaseActivity : Activity() {

    protected lateinit var theme: Theme
        private set

    protected lateinit var sound: SoundHelper
        private set

    private var lastSystemDark = false
    private lateinit var rootScroll: ScrollView
    private lateinit var rootColumn: LinearLayout
    private lateinit var topBar: LinearLayout
    private lateinit var contentColumn: LinearLayout

    protected abstract fun screenTitle(): String
    protected abstract fun buildContent(container: LinearLayout)

    protected open fun showBackButton(): Boolean = true
    protected open fun showThemeToggle(): Boolean = false
    protected open fun topBarTrailingAction(): Pair<String, () -> Unit>? = null
    protected open fun floatingActionView(): View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme = Theme.resolve(this)
        sound = SoundHelper(this)
        lastSystemDark = Theme.systemIsDark(this)
        buildScaffold()
    }

    override fun onResume() {
        super.onResume()
        if (AppPreferences.loadThemeChoice(this) == AppPreferences.ThemeChoice.SYSTEM) {
            val nowDark = Theme.systemIsDark(this)
            if (nowDark != lastSystemDark) recreate()
            lastSystemDark = nowDark
        }
    }

    override fun onDestroy() {
        sound.release()
        super.onDestroy()
    }

    private fun buildScaffold() {
        window.statusBarColor = theme.bg
        window.navigationBarColor = theme.bg
        val decor = window.decorView
        @Suppress("DEPRECATION")
        decor.systemUiVisibility = if (theme.isDark) 0 else
            (View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)

        rootScroll = ScrollView(this).apply {
            setBackgroundColor(theme.bg)
            clipToPadding = false
        }
        rootColumn = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(theme.bg)
        }
        rootScroll.addView(rootColumn)

        topBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(8), dp(10), dp(14), dp(10))
            background = solid(theme.surface)
            elevation = dp(2).toFloat()
        }

        if (showBackButton()) {
            topBar.addView(iconButton("←") { finish() }, LinearLayout.LayoutParams(dp(48), dp(48)))
        }

        val titleView = TextView(this).apply {
            text = screenTitle()
            textSize = 19f
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            setTextColor(theme.text)
            maxLines = 1
            setPadding(dp(10), 0, dp(8), 0)
        }
        topBar.addView(titleView, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))

        if (showThemeToggle()) {
            topBar.addView(
                iconButton("◐") { showThemeDialog() },
                LinearLayout.LayoutParams(dp(48), dp(48)).apply { leftMargin = dp(4) }
            )
        }

        topBarTrailingAction()?.let { (symbol, action) ->
            topBar.addView(
                iconButton(symbol, action),
                LinearLayout.LayoutParams(dp(48), dp(48)).apply { leftMargin = dp(4) }
            )
        }

        rootColumn.addView(topBar)

        contentColumn = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(18), dp(16), dp(28))
            setBackgroundColor(theme.bg)
        }
        rootColumn.addView(contentColumn)

        buildContent(contentColumn)

        val fab = floatingActionView()
        if (fab != null) {
            val overlay = FrameLayout(this)
            overlay.addView(rootScroll, FrameLayout.LayoutParams(-1, -1))
            overlay.addView(
                fab,
                FrameLayout.LayoutParams(dp(64), dp(64)).apply {
                    gravity = Gravity.BOTTOM or Gravity.END
                    rightMargin = dp(20)
                    bottomMargin = dp(28)
                }
            )
            setContentView(overlay)
        } else {
            setContentView(rootScroll)
        }
        applyInsets()
    }

    private fun applyInsets() {
        rootColumn.setOnApplyWindowInsetsListener { _, insets ->
            val bars = insets.getInsets(WindowInsets.Type.systemBars())
            topBar.setPadding(dp(8), bars.top + dp(8), dp(14), dp(10))
            contentColumn.setPadding(dp(16), dp(18), dp(16), bars.bottom + dp(28))
            insets
        }
        rootColumn.requestApplyInsets()
    }

    private fun showThemeDialog() {
        val names = arrayOf("System theme", "Light theme", "Dark theme")
        val current = AppPreferences.loadThemeChoice(this).ordinal
        AlertDialog.Builder(this)
            .setTitle("Choose theme")
            .setSingleChoiceItems(names, current) { dialog, which ->
                AppPreferences.saveThemeChoice(this, AppPreferences.ThemeChoice.values()[which])
                dialog.dismiss()
                recreate()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    // ---- Shared visual helpers for subclasses ----

    protected fun card(accent: Int? = null): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        background = if (accent != null) {
            roundedWithAccentEdge(theme.surface, 24, accent)
        } else {
            rounded(theme.surface, 24, theme.line, dp(1))
        }
        elevation = dp(1).toFloat()
    }

    protected fun placeholderCard(title: String, body: String): LinearLayout {
        val box = card()
        box.setPadding(dp(20), dp(20), dp(20), dp(20))
        box.addView(TextView(this).apply {
            text = title
            textSize = 18f
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            setTextColor(theme.text)
        }, wrap())
        box.addView(TextView(this).apply {
            text = body
            textSize = 14f
            setTextColor(theme.muted)
            setPadding(0, dp(8), 0, 0)
        }, wrap())
        return box
    }

    protected fun iconButton(symbol: String, onClick: () -> Unit): TextView = TextView(this).apply {
        text = symbol
        textSize = 20f
        gravity = Gravity.CENTER
        setTextColor(theme.text)
        background = rounded(theme.surfaceAlt, 16, theme.line, dp(1))
        isClickable = true
        isFocusable = true
        setOnClickListener { sound.playClick(); onClick() }
    }

    protected fun rounded(fill: Int, radius: Int, strokeColor: Int? = null, strokeWidth: Int = 0): GradientDrawable =
        GradientDrawable().apply {
            setColor(fill)
            cornerRadius = dp(radius).toFloat()
            if (strokeColor != null && strokeWidth > 0) setStroke(strokeWidth, strokeColor)
        }

    protected fun roundedWithAccentEdge(fill: Int, radius: Int, accent: Int): GradientDrawable =
        GradientDrawable().apply {
            setColor(fill)
            cornerRadius = dp(radius).toFloat()
            setStroke(dp(2), accent)
        }

    protected fun solid(fill: Int): GradientDrawable = GradientDrawable().apply { setColor(fill) }

    protected fun wrap() = LinearLayout.LayoutParams(-1, ViewGroup.LayoutParams.WRAP_CONTENT)

    protected fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
