package com.example.playlistmaker.di

import com.example.playlistmaker.db.converters.PlaylistsDbConvertor
import com.example.playlistmaker.db.converters.TrackDbConvertor
import com.example.playlistmaker.medialibrary.data.impl.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.medialibrary.domain.api.FavoriteTracksRepository
import com.example.playlistmaker.newplaylist.data.impl.ImageStorageRepositoryImpl
import com.example.playlistmaker.newplaylist.data.impl.PlaylistsRepositoryImpl
import com.example.playlistmaker.newplaylist.domain.api.ImageStorageRepository
import com.example.playlistmaker.newplaylist.domain.api.PlaylistsRepository
import com.example.playlistmaker.search.data.localstorage.HistoryRepositoryImpl
import com.example.playlistmaker.search.data.network.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.api.HistoryRepository
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    factory<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    factory<HistoryRepository> {
        HistoryRepositoryImpl(get())
    }

    factory<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    factory<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get(), get())
    }

    factory { TrackDbConvertor() }

    factory<PlaylistsRepository> {
        PlaylistsRepositoryImpl(get(), get(), get())
    }

    factory { PlaylistsDbConvertor() }

    factory<ImageStorageRepository> {
        ImageStorageRepositoryImpl(androidContext())
    }
}