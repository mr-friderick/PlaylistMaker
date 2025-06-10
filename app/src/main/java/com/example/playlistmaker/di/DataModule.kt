package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.settings.data.SettingsStorage
import com.example.playlistmaker.settings.data.SharedPrefSettingsStorage
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

}