package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.dto.TrackDto

interface HistoryStorage {
    fun save(tracksDto: List<TrackDto>)

    fun read(): List<TrackDto>

    fun clear()
}