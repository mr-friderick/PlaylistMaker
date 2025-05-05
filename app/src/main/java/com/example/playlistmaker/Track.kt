package com.example.playlistmaker

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TrackResponse(val results: ArrayList<Track>)

data class Track(
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
) {
    companion object {
        const val DEFAULT_TIME = "00:00"
    }

    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")

    fun getReleaseYear() = releaseDate.substringBefore("-")

    fun formatTrackTime(): String {
        return kotlin.runCatching {
            SimpleDateFormat("mm:ss", Locale.getDefault())
                .format(Date(trackTimeMillis.toLong()))
                .removePrefix("0")
        }.getOrDefault(DEFAULT_TIME)
    }
}