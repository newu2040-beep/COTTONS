package com.example.data

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object AssetImportHelper {

    fun saveImportedPhoto(context: Context, uri: Uri): String? {
        return try {
            val photosDir = File(context.filesDir, "imported_photos").apply { mkdirs() }
            val destFile = File(photosDir, "photo_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            destFile.absolutePath
        } catch (_: Exception) {
            null
        }
    }

    fun saveImportedAudio(context: Context, uri: Uri): String? {
        return try {
            val audioDir = File(context.filesDir, "imported_audio").apply { mkdirs() }
            val destFile = File(audioDir, "audio_${System.currentTimeMillis()}.mp3")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            destFile.absolutePath
        } catch (_: Exception) {
            null
        }
    }

    fun saveCustomSticker(context: Context, uri: Uri, name: String = "Sticker"): String? {
        return try {
            val stickerDir = File(context.filesDir, "custom_stickers").apply { mkdirs() }
            val destFile = File(stickerDir, "sticker_${System.currentTimeMillis()}.png")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            destFile.absolutePath
        } catch (_: Exception) {
            null
        }
    }
}
