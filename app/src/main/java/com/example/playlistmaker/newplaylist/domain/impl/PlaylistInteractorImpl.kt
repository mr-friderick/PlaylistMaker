package com.example.playlistmaker.newplaylist.domain.impl

import com.example.playlistmaker.newplaylist.domain.api.PlaylistsRepository
import com.example.playlistmaker.newplaylist.domain.interactors.PlaylistInteractor
import com.example.playlistmaker.newplaylist.domain.models.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlaylistInteractorImpl(
    private val repository: PlaylistsRepository
) : PlaylistInteractor {
    override suspend fun addPlaylist(playlist: Playlist) = withContext(Dispatchers.IO) {
        repository.addPlaylist(playlist)
    }

    override suspend fun updateTracksInPlaylist(
        playlistId: Int,
        tracksId: List<Int>
    ) = withContext(Dispatchers.IO) {
        repository.updateTracksInPlaylist(playlistId, tracksId)
    }
}