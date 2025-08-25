package com.example.playlistmaker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.db.dao.FavoriteTrackDao
import com.example.playlistmaker.db.dao.PlaylistsDao
import com.example.playlistmaker.db.dao.PlaylistTrackDao
import com.example.playlistmaker.db.entity.PlaylistEntity
import com.example.playlistmaker.db.entity.TrackEntity
import com.example.playlistmaker.db.entity.PlaylistTrackEntity

@Database(
    version = 1,
    entities = [TrackEntity::class, PlaylistEntity::class, PlaylistTrackEntity::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): FavoriteTrackDao

    abstract fun playlistsDao(): PlaylistsDao

    abstract fun playlistTrackDao(): PlaylistTrackDao
}