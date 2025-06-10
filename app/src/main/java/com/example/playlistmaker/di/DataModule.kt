package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.player.data.AudioPlayerClient
import com.example.playlistmaker.player.data.MediaPlayerController
import com.example.playlistmaker.player.data.MediaPlayerFactory
import com.example.playlistmaker.settings.data.SettingsStorage
import com.example.playlistmaker.settings.data.SharedPrefSettingsStorage
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val FILE_PREFERENCES = "local_preferences"

val dataModule = module {

    single {
        androidContext().getSharedPreferences(FILE_PREFERENCES, Context.MODE_PRIVATE)
    }

    single<SettingsStorage> {
        SharedPrefSettingsStorage(get())
    }

    single<AudioPlayerClient> {
        MediaPlayerController(get())
    }

    single {
        MediaPlayerFactory()
    }

    single {
        Gson()
    }

}