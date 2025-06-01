package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.dto.TrackDto

interface HistoryStorage {
    fun save(tracksDto: ArrayList<TrackDto>)

    fun read(): ArrayList<TrackDto>

    fun clear()
}