package com.example.playlistmaker.medialibrary.domain.impl

import com.example.playlistmaker.medialibrary.domain.api.FavoriteTracksRepository
import com.example.playlistmaker.medialibrary.domain.interactors.FavoriteTracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class FavoriteTracksInteractorImpl(
    private val repository: FavoriteTracksRepository
): FavoriteTracksInteractor {

    override suspend fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override suspend fun deleteTrack(trackId: Int) {
        repository.deleteTrack(trackId)
    }

    override fun getAll(): Flow<List<Track>> {
        return repository.getAll()
    }

    override suspend fun isFavorite(trackId: Int): Boolean = withContext(Dispatchers.IO) {
        repository.isFavorite(trackId)
    }

}