package com.example.playlistmaker.medialibrary.data.impl

import com.example.playlistmaker.db.AppDatabase
import com.example.playlistmaker.db.converters.TrackDbConvertor
import com.example.playlistmaker.medialibrary.domain.api.FavoriteTracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTracksRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor
): FavoriteTracksRepository {

    override suspend fun addTrack(track: Track) {
        appDatabase.trackDao().insertTrack(
            trackDbConvertor.map(track)
        )
    }

    override suspend fun deleteTrack(trackId: Int) {
        appDatabase.trackDao().deleteTrackById(trackId)
    }

    override fun getAll(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().selectAll()
        emit(tracks
            .sortedByDescending { it.addTime }
            .map { track -> trackDbConvertor.map(track) }
        )
    }

    override suspend fun isFavorite(trackId: Int): Boolean {
        return appDatabase.trackDao().selectIsFavorite(trackId)
    }

}