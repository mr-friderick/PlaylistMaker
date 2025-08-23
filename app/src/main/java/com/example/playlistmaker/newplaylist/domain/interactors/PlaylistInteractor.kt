package com.example.playlistmaker.newplaylist.domain.interactors

import com.example.playlistmaker.newplaylist.domain.models.Playlist

interface PlaylistInteractor {
    suspend fun addPlaylist(playlist: Playlist)

    suspend fun updateTracksInPlaylist(playlistId: Int, tracksId: List<Int>)
}