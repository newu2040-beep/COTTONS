package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("cottons_prefs", Context.MODE_PRIVATE)

    private val _themeName = MutableStateFlow(prefs.getString(KEY_THEME, "Picnic Red") ?: "Picnic Red")
    val themeName: StateFlow<String> = _themeName.asStateFlow()

    private val _userName = MutableStateFlow(prefs.getString(KEY_USER_NAME, "Rahul") ?: "Rahul")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _soundEnabled = MutableStateFlow(prefs.getBoolean(KEY_SOUND, true))
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _hapticsEnabled = MutableStateFlow(prefs.getBoolean(KEY_HAPTICS, true))
    val hapticsEnabled: StateFlow<Boolean> = _hapticsEnabled.asStateFlow()

    private val _onboarded = MutableStateFlow(prefs.getBoolean(KEY_ONBOARDED, false))
    val onboarded: StateFlow<Boolean> = _onboarded.asStateFlow()

    private val _darkMode = MutableStateFlow(prefs.getString(KEY_DARK_MODE, "SYSTEM") ?: "SYSTEM")
    val darkMode: StateFlow<String> = _darkMode.asStateFlow()

    private val _fontChoice = MutableStateFlow(prefs.getString(KEY_FONT_CHOICE, "CURSIVE") ?: "CURSIVE")
    val fontChoice: StateFlow<String> = _fontChoice.asStateFlow()

    private val _customAudioPath = MutableStateFlow<String?>(prefs.getString(KEY_CUSTOM_AUDIO, null))
    val customAudioPath: StateFlow<String?> = _customAudioPath.asStateFlow()

    fun setTheme(theme: String) {
        prefs.edit().putString(KEY_THEME, theme).apply()
        _themeName.value = theme
    }

    fun setUserName(name: String) {
        val trimmed = name.trim().ifBlank { "Friend" }
        prefs.edit().putString(KEY_USER_NAME, trimmed).apply()
        _userName.value = trimmed
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND, enabled).apply()
        _soundEnabled.value = enabled
    }

    fun setHapticsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTICS, enabled).apply()
        _hapticsEnabled.value = enabled
    }

    fun setOnboarded(done: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDED, done).apply()
        _onboarded.value = done
    }

    fun setDarkMode(mode: String) {
        prefs.edit().putString(KEY_DARK_MODE, mode).apply()
        _darkMode.value = mode
    }

    fun setFontChoice(font: String) {
        prefs.edit().putString(KEY_FONT_CHOICE, font).apply()
        _fontChoice.value = font
    }

    fun setCustomAudio(path: String?) {
        prefs.edit().putString(KEY_CUSTOM_AUDIO, path).apply()
        _customAudioPath.value = path
    }

    companion object {
        private const val KEY_THEME = "key_theme"
        private const val KEY_USER_NAME = "key_user_name"
        private const val KEY_SOUND = "key_sound"
        private const val KEY_HAPTICS = "key_haptics"
        private const val KEY_ONBOARDED = "key_onboarded"
        private const val KEY_DARK_MODE = "key_dark_mode"
        private const val KEY_FONT_CHOICE = "key_font_choice"
        private const val KEY_CUSTOM_AUDIO = "key_custom_audio"
    }
}
