package com.example.playlistmaker.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val picturePath: String,
    var tracksCount: Int = 0
)