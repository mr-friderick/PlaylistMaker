package com.example.playlistmaker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.db.entity.TrackEntity

@Dao
interface FavoriteTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrack(track: TrackEntity)

    @Query("DELETE FROM favorite_track_table WHERE trackId = :trackId")
    fun deleteTrackById(trackId: Int)

    @Query("SELECT * FROM favorite_track_table")
    suspend fun selectAll(): List<TrackEntity>

    @Query("SELECT 1 FROM favorite_track_table WHERE trackId = :trackId")
    fun selectIsFavorite(trackId: Int): Boolean
}