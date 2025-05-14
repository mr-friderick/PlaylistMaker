package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.TrackDto

interface HistoryStorage {
    fun save(tracksDto: ArrayList<TrackDto>)

    fun read(): ArrayList<TrackDto>

    fun clear()
}