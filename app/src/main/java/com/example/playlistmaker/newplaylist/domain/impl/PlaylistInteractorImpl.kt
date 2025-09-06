package com.example.playlistmaker.newplaylist.domain.impl

import com.example.playlistmaker.newplaylist.domain.api.PlaylistsRepository
import com.example.playlistmaker.newplaylist.domain.interactors.PlaylistInteractor
import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PlaylistInteractorImpl(
    private val repository: PlaylistsRepository
) : PlaylistInteractor {

    override suspend fun addPlaylist(playlist: Playlist) = withContext(Dispatchers.IO) {
        repository.addPlaylist(playlist)
    }

    override suspend fun getPlaylist(playlistId: Int): Playlist = withContext(Dispatchers.IO) {
        repository.getPlaylist(playlistId)
    }

    override suspend fun deletePlaylist(playlistId: Int) = withContext(Dispatchers.IO) {
        repository.deletePlaylist(playlistId)
    }

    override suspend fun updatePlaylist(playlist: Playlist) = withContext(Dispatchers.IO) {
        repository.updatePlaylist(playlist)
    }

    override suspend fun isTrackInPlaylist(playlistId: Int, trackId: Int): Boolean = withContext(Dispatchers.IO) {
        repository.isTrackInPlaylist(playlistId, trackId)
    }

    override suspend fun addTrackToPlaylist(playlistId: Int, trackId: Int) = withContext(Dispatchers.IO) {
        repository.addTrackToPlaylist(playlistId, trackId)
    }

    override suspend fun getTracksCountInPlaylist(playlistId: Int): Int = withContext(Dispatchers.IO) {
        repository.getTracksCountInPlaylist(playlistId)
    }

    override suspend fun getTracksForPlaylist(playlistId: Int): Flow<List<Track>> {
        return repository.getTracksForPlaylist(playlistId)
    }

    override suspend fun deleteTrackFromPlaylist(playlistId: Int, trackId: Int) = withContext(Dispatchers.IO) {
        repository.deleteTrackFromPlaylist(playlistId, trackId)
    }

    override suspend fun addPlaylistTrack(track: Track) = withContext(Dispatchers.IO) {
        repository.addPlaylistTrack(track)
    }

    override fun getAll(): Flow<List<Playlist>> {
        return repository.getAll()
    }
}