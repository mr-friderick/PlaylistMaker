package com.example.playlistmaker.newplaylist.data.impl

import com.example.playlistmaker.db.converters.PlaylistsDbConvertor
import com.example.playlistmaker.db.converters.TrackDbConvertor
import com.example.playlistmaker.db.dao.PlaylistsDao
import com.example.playlistmaker.db.entity.PlaylistTrackCrossRef
import com.example.playlistmaker.newplaylist.domain.api.PlaylistsRepository
import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsRepositoryImpl(
    private val dao: PlaylistsDao,
    private val dbPlaylistConvertor: PlaylistsDbConvertor,
    private val dbPlaylistTrackConvertor: TrackDbConvertor
): PlaylistsRepository {

    override suspend fun addPlaylist(playlist: Playlist) {
        dao.insertPlaylist(
            dbPlaylistConvertor.map(playlist)
        )
    }

    override suspend fun getPlaylist(playlistId: Int): Playlist {
        return dbPlaylistConvertor.map(
            dao.selectPlaylist(playlistId)
        )
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        dao.updatePlaylist(
            dbPlaylistConvertor.map(playlist)
        )
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        dao.deletePlaylistAndUnownedTracks(playlistId)
    }

    override fun getAll(): Flow<List<Playlist>> = flow {
        val playlists = dao.selectAll()
        for (entity in playlists) {
            entity.tracksCount = dao.selectTracksCountInPlaylist(entity.id)
        }
        emit(playlists
            .map { playlist -> dbPlaylistConvertor.map(playlist) }
        )
    }

    override suspend fun isTrackInPlaylist(playlistId: Int, trackId: Int): Boolean {
        return dao.isTrackInPlaylist(playlistId, trackId)
    }

    override suspend fun addTrackToPlaylist(playlistId: Int, trackId: Int) {
        dao.insertTrackToPlaylist(PlaylistTrackCrossRef(playlistId, trackId))
    }

    override suspend fun getTracksCountInPlaylist(playlistId: Int): Int {
        return dao.selectTracksCountInPlaylist(playlistId)
    }

    override suspend fun getTracksForPlaylist(playlistId: Int): Flow<List<Track>> = flow {
        val tracks = dao.selectPlaylistWithTracks(playlistId).tracks
        emit(tracks
            .sortedByDescending { it.addTime }
            .map { track -> dbPlaylistTrackConvertor.mapPlaylistTrack(track) }
        )
    }

    override suspend fun deleteTrackFromPlaylist(playlistId: Int, trackId: Int) {
        dao.deleteTrackFromPlaylist(playlistId, trackId)

        if (!dao.isTrackInAnyPlaylist(trackId)) {
            dao.deleteTrack(trackId)
        }
    }

    override suspend fun addPlaylistTrack(track: Track) {
        dao.insertTrack(
            dbPlaylistTrackConvertor.mapPlaylistTrack(track)
        )
    }

}