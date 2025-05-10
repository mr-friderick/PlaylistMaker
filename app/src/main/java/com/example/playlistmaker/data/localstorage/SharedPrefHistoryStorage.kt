package com.example.playlistmaker.data.localstorage

import android.content.SharedPreferences
import com.example.playlistmaker.data.HistoryStorage
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

class SharedPrefHistoryStorage(
    private val sharedPrefs: SharedPreferences,
    private val gson: Gson
): HistoryStorage {
    companion object {
        const val KEY_HISTORY = "tracks"
    }

    override fun save(tracksDto: ArrayList<TrackDto>) {
        sharedPrefs.edit {
            putString(
                KEY_HISTORY,
                Gson().toJson(tracksDto)
            )
        }
    }

    override fun read(): ArrayList<TrackDto> {
        return gson.fromJson(
            sharedPrefs.getString(KEY_HISTORY, ""),
            object : TypeToken<List<TrackDto>>() {}.type
        ) ?: arrayListOf()
    }

    override fun clear() {
        sharedPrefs.edit {
            remove(KEY_HISTORY)
        }
    }
}