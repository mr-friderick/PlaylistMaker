package com.example.playlistmaker.data.localstorage

import com.example.playlistmaker.data.HistoryStorage
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.api.HistoryRepository
import com.example.playlistmaker.domain.models.Track

class HistoryRepositoryImpl(private val historyStorage: HistoryStorage): HistoryRepository {
    override fun save(tracks: ArrayList<Track>) {
        historyStorage.save(
            tracks.map {
                TrackDto(
                    trackId = it.trackId,
                    trackName = it.trackName,
                    artistName = it.artistName,
                    trackTimeMillis = it.trackTimeToMillis(),
                    artworkUrl100 = it.artworkUrl100,
                    collectionName = it.collectionName,
                    releaseDate = it.releaseDate,
                    primaryGenreName = it.primaryGenreName,
                    country = it.country,
                    previewUrl = it.previewUrl
                )
            }.toCollection(ArrayList())
        )
    }

    override fun read(): ArrayList<Track> {
        return historyStorage.read()
            .map {
                Track(
                    trackId = it.trackId,
                    trackName = it.trackName,
                    artistName = it.artistName,
                    trackTimeMillis = it.trackTimeToMMSS(),
                    artworkUrl100 = it.artworkUrl100,
                    collectionName = it.collectionName,
                    releaseDate = it.releaseDate,
                    primaryGenreName = it.primaryGenreName,
                    country = it.country,
                    previewUrl = it.previewUrl
                )
            }.toCollection(ArrayList())
    }

    override fun clear() {
        historyStorage.clear()
    }
}