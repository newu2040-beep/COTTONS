package com.example.data

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

class SoundEffectsPlayer(private val context: Context, private val prefs: PreferencesManager) {
    private val scope = CoroutineScope(Dispatchers.Default)

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    fun playTapeClick() {
        vibrate(30, 180)
        if (prefs.soundEnabled.value) {
            playTone(frequency = 720.0, durationMs = 28, click = true)
        }
    }

    fun playStampThunk() {
        vibrate(45, 255)
        if (prefs.soundEnabled.value) {
            playTone(frequency = 180.0, durationMs = 60, click = false)
        }
    }

    fun playPaperRustle() {
        vibrate(20, 120)
        if (prefs.soundEnabled.value) {
            playTone(frequency = 440.0, durationMs = 35, click = true)
        }
    }

    fun playWaxSealSquish() {
        vibrate(55, 240)
        if (prefs.soundEnabled.value) {
            playTone(frequency = 260.0, durationMs = 80, click = false)
        }
    }

    private fun vibrate(durationMs: Long, amplitude: Int) {
        if (!prefs.hapticsEnabled.value) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(durationMs, amplitude.coerceIn(1, 255))
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    private fun playTone(frequency: Double, durationMs: Int, click: Boolean) {
        scope.launch {
            try {
                val sampleRate = 22050
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
                val buffer = ShortArray(numSamples)

                for (i in buffer.indices) {
                    val progress = i.toDouble() / numSamples
                    val env = (1.0 - progress) * (1.0 - progress) // exponential decay
                    val freq = if (click) frequency * (1.0 - progress * 0.4) else frequency
                    val sample = sin(2.0 * Math.PI * i * freq / sampleRate) * env * 0.4
                    buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
                }

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(buffer, 0, buffer.size)
                track.play()
                track.setNotificationMarkerPosition(numSamples)
                track.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
                    override fun onPeriodicNotification(track: AudioTrack?) {}
                    override fun onMarkerReached(track: AudioTrack?) {
                        try {
                            track?.release()
                        } catch (_: Exception) {}
                    }
                })
            } catch (_: Exception) {}
        }
    }
}
