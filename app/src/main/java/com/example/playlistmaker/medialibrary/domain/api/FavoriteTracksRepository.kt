package com.example.playlistmaker.medialibrary.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {

    suspend fun addTrack(track: Track)

    suspend fun deleteTrack(trackId: Int)

    fun getAll(): Flow<List<Track>>

    suspend fun isFavorite(trackId: Int): Boolean

}