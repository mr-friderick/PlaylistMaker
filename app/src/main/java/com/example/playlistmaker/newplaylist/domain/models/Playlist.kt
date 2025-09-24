package com.example.playlistmaker.newplaylist.domain.models

data class Playlist(
    val id: Int,
    val title: String,
    val description: String,
    val picturePath: String,
    var tracksCount: Int = 0
)