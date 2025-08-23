package com.example.playlistmaker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.db.entity.PlaylistEntity

@Dao
interface PlaylistsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlaylist(playlist: PlaylistEntity)

    @Query("UPDATE playlists_table SET tracksId = :tracksId WHERE id = :playlistId")
    fun updateTracksInPlaylist(playlistId: Int, tracksId: String)
}