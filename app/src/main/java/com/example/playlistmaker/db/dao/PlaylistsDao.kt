package com.example.playlistmaker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.playlistmaker.db.entity.PlaylistEntity
import com.example.playlistmaker.db.entity.PlaylistTrackCrossRef
import com.example.playlistmaker.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.db.relationship.PlaylistWithTracks
import com.example.playlistmaker.db.relationship.TrackWithPlaylists

@Dao
interface PlaylistsDao {

    // ---- Методы сущностей ----
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlaylist(playlist: PlaylistEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTrack(track: PlaylistTrackEntity)

    @Query("SELECT * FROM playlists_table")
    suspend fun selectAll(): List<PlaylistEntity>

    // ---- Методы связей ----
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTrackToPlaylist(crossRef: PlaylistTrackCrossRef)

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM playlist_track_cross_ref
            WHERE playlistId = :playlistId AND trackId = :trackId
        )
    """)
    suspend fun isTrackInPlaylist(playlistId: Int, trackId: Int): Boolean

    @Query("SELECT COUNT(*) FROM playlist_track_cross_ref WHERE playlistId = :playlistId")
    suspend fun selectTracksCountInPlaylist(playlistId: Int): Int

    // ---- Методы отношений ----
    @Transaction
    @Query("SELECT * FROM playlists_table WHERE id = :playlistId")
    suspend fun selectPlaylistWithTracks(playlistId: Int): PlaylistWithTracks

    @Transaction
    @Query("SELECT * FROM tracks_in_playlists_table WHERE trackId = :trackId")
    suspend fun selectTrackWithPlaylists(trackId: Int): TrackWithPlaylists





    // ---- TODO НА РАЗРБОР ----

    @Query("DELETE FROM playlist_track_cross_ref WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun deleteTrackFromPlaylist(playlistId: Int, trackId: Int)

}