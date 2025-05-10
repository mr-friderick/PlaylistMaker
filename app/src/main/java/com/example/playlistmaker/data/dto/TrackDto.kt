package com.example.playlistmaker.data.dto

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TrackDto(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
) {
    companion object {
        const val DEFAULT_TIME = "00:00"
    }

    fun formatTrackTime(): String {
        return kotlin.runCatching {
            SimpleDateFormat("mm:ss", Locale.getDefault())
                .format(Date(trackTimeMillis))
                .removePrefix("0")
        }.getOrDefault(DEFAULT_TIME)
    }
}
