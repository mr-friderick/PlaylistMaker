package com.example.playlistmaker.newplaylist.domain.api

import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    suspend fun addPlaylist(playlist: Playlist)

    suspend fun addPlaylistTrack(track: Track)

    suspend fun updateTracksInPlaylist(playlistId: Int, tracksId: List<Int>)

    fun getAll(): Flow<List<Playlist>>

}