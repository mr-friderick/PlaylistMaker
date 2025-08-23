package com.example.playlistmaker.newplaylist.data.impl

import com.example.playlistmaker.db.converters.PlaylistsDbConvertor
import com.example.playlistmaker.db.dao.PlaylistsDao
import com.example.playlistmaker.newplaylist.domain.api.PlaylistsRepository
import com.example.playlistmaker.newplaylist.domain.models.Playlist

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
}