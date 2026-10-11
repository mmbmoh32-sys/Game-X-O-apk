package com.example.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SoundManager(context: Context) {
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private var toneGenerator: ToneGenerator? = try {
        ToneGenerator(AudioManager.STREAM_MUSIC, 75)
    } catch (_: Exception) {
        null
    }

    private val scope = CoroutineScope(Dispatchers.Default)

    var isSoundEnabled: Boolean = true
    var isHapticsEnabled: Boolean = true

    fun playMoveX() {
        if (isSoundEnabled) {
            scope.launch {
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_D, 50)
            }
        }
        triggerHaptic(30)
    }

    fun playMoveO() {
        if (isSoundEnabled) {
            scope.launch {
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_A, 50)
            }
        }
        triggerHaptic(30)
    }

    fun playWin() {
        if (isSoundEnabled) {
            scope.launch {
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_1, 80)
                delay(90)
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_5, 80)
                delay(90)
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_9, 140)
            }
        }
        triggerWinHaptic()
    }

    fun playLoss() {
        if (isSoundEnabled) {
            scope.launch {
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_7, 90)
                delay(100)
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_4, 90)
                delay(100)
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_1, 130)
            }
        }
        triggerHaptic(80)
    }

    fun playDraw() {
        if (isSoundEnabled) {
            scope.launch {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 100)
            }
        }
        triggerHaptic(40)
    }

    fun playButtonClick() {
        if (isSoundEnabled) {
            scope.launch {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 30)
            }
        }
        triggerHaptic(15)
    }

    fun playReaction() {
        if (isSoundEnabled) {
            scope.launch {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 40)
            }
        }
        triggerHaptic(20)
    }

    private fun triggerHaptic(durationMs: Long) {
        if (!isHapticsEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    private fun triggerWinHaptic() {
        if (!isHapticsEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 40, 60, 60, 60, 100)
                val amplitudes = intArrayOf(0, 150, 0, 200, 0, 255)
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(150)
            }
        } catch (_: Exception) {}
    }

    fun release() {
        toneGenerator?.release()
        toneGenerator = null
    }
}
