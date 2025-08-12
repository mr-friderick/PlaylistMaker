package com.example.playlistmaker.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.db.entity.TrackEntity

@Dao
interface FavoriteTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrack(track: TrackEntity)

    @Query("DELETE FROM track_table WHERE trackId = :trackId")
    fun deleteTrackById(trackId: Int)

    @Query("SELECT * FROM track_table WHERE isFavorite")
    suspend fun selectAll(): List<TrackEntity>

    @Query("SELECT 1 FROM track_table WHERE isFavorite AND trackId = :trackId")
    fun selectIsFavorite(trackId: Int): Boolean
}