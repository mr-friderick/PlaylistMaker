package com.example.playlistmaker.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.playlistmaker.search.data.dto.TrackDto.Companion.DEFAULT_TIME
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "tracks_in_playlists_table")
data class PlaylistTrackEntity(
    @PrimaryKey
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
)
