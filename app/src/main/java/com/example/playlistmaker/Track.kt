package com.example.playlistmaker

data class TrackResponse(val results: ArrayList<Track>)

data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String
)