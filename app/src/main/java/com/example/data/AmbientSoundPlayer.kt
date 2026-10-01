package com.example.data

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.util.Log
import com.example.model.AmbientSound
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import kotlin.random.Random

class AmbientSoundPlayer {
    private var audioTrack: AudioTrack? = null
    private var mediaPlayer: MediaPlayer? = null
    private var playJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    fun play(sound: AmbientSound) {
        stop()
        if (sound == AmbientSound.NONE) return

        playJob = scope.launch {
            try {
                val sampleRate = 22050
                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                val bufferSize = (sampleRate / 4).coerceAtLeast(minBufferSize)

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack = track
                track.play()

                val buffer = ShortArray(bufferSize)
                val rnd = Random(42)

                var b0 = 0.0
                var b1 = 0.0
                var b2 = 0.0
                var b3 = 0.0
                var b4 = 0.0
                var b5 = 0.0
                var b6 = 0.0
                var phase = 0.0

                while (isActive) {
                    for (i in buffer.indices) {
                        val white = rnd.nextDouble() * 2.0 - 1.0

                        val sample = when (sound) {
                            AmbientSound.RAIN -> {
                                b0 = 0.99886 * b0 + white * 0.0555179
                                b1 = 0.99332 * b1 + white * 0.0750759
                                b2 = 0.96900 * b2 + white * 0.1538520
                                b3 = 0.86650 * b3 + white * 0.3104856
                                b4 = 0.55000 * b4 + white * 0.5329522
                                b5 = -0.7616 * b5 - white * 0.0168980
                                val pink = b0 + b1 + b2 + b3 + b4 + b5 + b6 + white * 0.5362
                                b6 = white * 0.115926
                                val drop = if (rnd.nextDouble() < 0.003) (rnd.nextDouble() * 0.8 - 0.4) else 0.0
                                (pink * 0.08 + drop)
                            }
                            AmbientSound.WHITE -> {
                                white * 0.06
                            }
                            AmbientSound.LOFI -> {
                                phase += 2.0 * Math.PI * 60.0 / sampleRate
                                val hum = Math.sin(phase) * 0.04
                                b0 = 0.95 * b0 + 0.05 * white
                                (b0 * 0.12 + hum + if (rnd.nextDouble() < 0.001) 0.1 else 0.0)
                            }
                            AmbientSound.FOREST -> {
                                phase += 2.0 * Math.PI * 0.2 / sampleRate
                                val swell = (Math.sin(phase) + 1.2) * 0.5
                                b0 = 0.92 * b0 + 0.08 * white
                                (b0 * 0.08 * swell)
                            }
                            AmbientSound.CAFE -> {
                                b0 = 0.96 * b0 + 0.04 * white
                                b1 = 0.92 * b1 + 0.08 * b0
                                (b1 * 0.15 + if (rnd.nextDouble() < 0.0015) (rnd.nextDouble() * 0.2) else 0.0)
                            }
                            AmbientSound.NONE -> 0.0
                        }

                        val shortVal = (sample.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
                        buffer[i] = shortVal
                    }
                    track.write(buffer, 0, buffer.size)
                }
            } catch (e: Exception) {
                Log.e("AmbientPlayer", "Audio error", e)
            } finally {
                try {
                    audioTrack?.stop()
                    audioTrack?.release()
                } catch (_: Exception) {}
                audioTrack = null
            }
        }
    }

    fun playCustomAudio(filePath: String) {
        stop()
        try {
            val file = File(filePath)
            if (!file.exists()) return

            mediaPlayer = MediaPlayer().apply {
                setDataSource(filePath)
                isLooping = true
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e("AmbientPlayer", "Custom audio error", e)
        }
    }

    fun stop() {
        playJob?.cancel()
        playJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null

        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
    }
}
