package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.ArrayList

class SearchHistory(private val sharedPrefs: SharedPreferences) {

    private val tracks: ArrayList<Track> = Gson().fromJson(
        sharedPrefs.getString(TRACKS, ""),
        object : TypeToken<List<Track>>() {}.type
    ) ?: arrayListOf()

    companion object {
        const val HISTORY_PREFERENCES = "history_preferences"
        const val TRACKS = "tracks"
        const val MAX_SIZE = 10
    }

    fun add(track: Track) {
        tracks.removeIf { it.trackId == track.trackId }
        if (tracks.size == MAX_SIZE) {
            tracks.removeAt(0)
        }
        tracks.add(track)
    }

    fun safe() {
        sharedPrefs.edit()
            .putString(
                TRACKS,
                Gson().toJson(tracks)
            )
            .apply()
    }

    fun read() {

    }

    fun clear() {
        tracks.clear()
        sharedPrefs.edit()
            .putString(TRACKS, "")
            .apply()
    }

    fun trackList() = ArrayList(tracks.reversed())
}