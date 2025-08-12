package com.example.playlistmaker.search.domain.models

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
    val previewUrl: String,
    var isFavorite: Boolean = false
) {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")

    fun getReleaseYear() = releaseDate.substringBefore("-")

    fun trackTimeToMillis(): Long {
        return kotlin.runCatching {
            val parts = trackTimeMillis.split(":").map { it.toInt() }
            val minutes = parts.getOrNull(0) ?: 0
            val seconds = parts.getOrNull(1) ?: 0
            (minutes * 60 + seconds) * 1000L
        }.getOrDefault(0L)
    }
}