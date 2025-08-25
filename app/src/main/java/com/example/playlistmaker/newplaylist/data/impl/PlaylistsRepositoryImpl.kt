package com.example.playlistmaker.newplaylist.data.impl

import com.example.playlistmaker.db.converters.PlaylistsDbConvertor
import com.example.playlistmaker.db.converters.TrackDbConvertor
import com.example.playlistmaker.db.dao.PlaylistTrackDao
import com.example.playlistmaker.db.dao.PlaylistsDao
import com.example.playlistmaker.newplaylist.domain.api.PlaylistsRepository
import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsRepositoryImpl(
    private val daoPlaylist: PlaylistsDao,
    private val daoPlaylistTrackDao: PlaylistTrackDao,
    private val dbPlaylistConvertor: PlaylistsDbConvertor,
    private val dbPlaylistTrackConvertor: TrackDbConvertor
): PlaylistsRepository {

    override suspend fun addPlaylist(playlist: Playlist) {
        daoPlaylist.insertPlaylist(
            dbPlaylistConvertor.map(playlist)
        )
    }

    override suspend fun addPlaylistTrack(track: Track) {
        daoPlaylistTrackDao.insertTrack(
            dbPlaylistTrackConvertor.mapPlaylistTrack(track)
        )
    }

    override suspend fun updateTracksInPlaylist(playlistId: Int, tracksId: List<Int>) {
        daoPlaylist.updateTracksInPlaylist(
            playlistId,
            dbPlaylistConvertor.mapTracksId(tracksId),
            tracksId.size
        )
    }

    override fun getAll(): Flow<List<Playlist>> = flow {
        val playlists = daoPlaylist.selectAll()
        emit(playlists
            .map { playlist -> dbPlaylistConvertor.map(playlist) }
        )
    }
}