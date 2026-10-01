package com.example

import android.app.Application
import com.example.data.AmbientSoundPlayer
import com.example.data.CottonsDatabase
import com.example.data.CottonsRepository
import com.example.data.PreferencesManager
import com.example.data.SoundEffectsPlayer

class AppContainer(application: Application) {
    val database = CottonsDatabase.getInstance(application)
    val prefs = PreferencesManager(application)
    val repository = CottonsRepository(database)
    val ambientPlayer = AmbientSoundPlayer()
    val sounds = SoundEffectsPlayer(application, prefs)
}

class CottonsApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
