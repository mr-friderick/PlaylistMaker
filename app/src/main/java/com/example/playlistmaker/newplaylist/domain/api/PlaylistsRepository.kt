package com.example.playlistmaker.newplaylist.domain.api

import com.example.playlistmaker.newplaylist.domain.models.Playlist

interface PlaylistsRepository {
    suspend fun addPlaylist(playlist: Playlist)

    suspend fun updateTracksInPlaylist(playlistId: Int, tracksId: List<Int>)
}