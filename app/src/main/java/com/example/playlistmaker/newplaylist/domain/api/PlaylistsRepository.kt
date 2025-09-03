package com.example.playlistmaker.newplaylist.domain.api

import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    suspend fun addPlaylist(playlist: Playlist)

    suspend fun isTrackInPlaylist(playlistId: Int, trackId: Int): Boolean

    suspend fun addTrackToPlaylist(playlistId: Int, trackId: Int)

    suspend fun getTracksCountInPlaylist(playlistId: Int): Int

    suspend fun addPlaylistTrack(track: Track)

    fun getAll(): Flow<List<Playlist>>

}