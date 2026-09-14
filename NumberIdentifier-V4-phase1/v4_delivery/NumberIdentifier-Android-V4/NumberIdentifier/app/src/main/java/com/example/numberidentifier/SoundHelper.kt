package com.example.numberidentifier

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator

/**
 * Plays a short UI click tone when Settings > Sound effects is on.
 * Uses ToneGenerator so the app doesn't need any bundled audio files.
 */
class SoundHelper(private val context: Context) {

    private var toneGenerator: ToneGenerator? = null

    private fun generator(): ToneGenerator? {
        if (!AppPreferences.isSoundEnabled(context)) return null
        if (toneGenerator == null) {
            toneGenerator = try {
                ToneGenerator(AudioManager.STREAM_MUSIC, 60)
            } catch (e: RuntimeException) {
                null
            }
        }
        return toneGenerator
    }

    fun playClick() {
        generator()?.startTone(ToneGenerator.TONE_PROP_BEEP2, 40)
    }

    fun release() {
        toneGenerator?.release()
        toneGenerator = null
    }
}
