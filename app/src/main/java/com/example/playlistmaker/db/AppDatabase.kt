package com.example.playlistmaker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.db.dao.FavoriteTrackDao
import com.example.playlistmaker.db.entity.TrackEntity

@Database(
    version = 1,
    entities = [TrackEntity::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): FavoriteTrackDao
}