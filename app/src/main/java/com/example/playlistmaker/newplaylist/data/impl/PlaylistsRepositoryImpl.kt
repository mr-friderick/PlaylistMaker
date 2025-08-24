package com.example.playlistmaker.newplaylist.data.impl

import com.example.playlistmaker.db.converters.PlaylistsDbConvertor
import com.example.playlistmaker.db.dao.PlaylistsDao
import com.example.playlistmaker.newplaylist.domain.api.PlaylistsRepository
import com.example.playlistmaker.newplaylist.domain.models.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsRepositoryImpl(
    private val dao: PlaylistsDao,
    private val dbConvertor: PlaylistsDbConvertor
): PlaylistsRepository {
    override suspend fun addPlaylist(playlist: Playlist) {
        dao.insertPlaylist(
            dbConvertor.map(playlist)
        )
    }

    override suspend fun updateTracksInPlaylist(playlistId: Int, tracksId: List<Int>) {
        dao.updateTracksInPlaylist(
            playlistId,
            dbConvertor.mapTracksId(tracksId)
        )
    }

    override fun getAll(): Flow<List<Playlist>> = flow {
        val playlists = dao.selectAll()
        emit(playlists
            .sortedByDescending { it.tracksId }
            .map { playlist -> dbConvertor.map(playlist) }
        )
    }
}