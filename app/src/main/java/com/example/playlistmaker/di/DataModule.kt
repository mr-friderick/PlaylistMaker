package com.example.playlistmaker.di

import android.content.Context
import androidx.room.Room
import com.example.playlistmaker.db.AppDatabase
import com.example.playlistmaker.db.dao.FavoriteTrackDao
import com.example.playlistmaker.db.dao.PlaylistsDao
import com.example.playlistmaker.player.data.AudioPlayerClient
import com.example.playlistmaker.player.data.MediaPlayerController
import com.example.playlistmaker.player.data.MediaPlayerFactory
import com.example.playlistmaker.player.data.MediaPlayerFactoryImpl
import com.example.playlistmaker.search.data.HistoryStorage
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.localstorage.SharedPrefHistoryStorage
import com.example.playlistmaker.search.data.network.ItunesAPI
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.settings.data.SettingsStorage
import com.example.playlistmaker.settings.data.SharedPrefSettingsStorage
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val FILE_PREFERENCES = "local_preferences"
private const val ITUNES_BASE_URL_SEARCH = "https://itunes.apple.com/"

val dataModule = module {

    single<SettingsStorage> {
        SharedPrefSettingsStorage(get())
    }

    factory<AudioPlayerClient> {
        MediaPlayerController(get())
    }

    single<HistoryStorage> {
        SharedPrefHistoryStorage(get(), get())
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    single {
        androidContext().getSharedPreferences(FILE_PREFERENCES, Context.MODE_PRIVATE)
    }

    factory<MediaPlayerFactory> {
        MediaPlayerFactoryImpl()
    }

    single {
        Gson()
    }

    single<ItunesAPI> {
        Retrofit.Builder()
            .baseUrl(ITUNES_BASE_URL_SEARCH)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesAPI::class.java)
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "dbase8.db")
            .build()
    }

    single<FavoriteTrackDao> {
        get<AppDatabase>().trackDao()
    }

    single<PlaylistsDao> {
        get<AppDatabase>().playlistsDao()
    }
}