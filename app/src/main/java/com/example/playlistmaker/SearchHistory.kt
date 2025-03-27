package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.ArrayList

class SearchHistory(private val sharedPrefs: SharedPreferences) {

    private val tracks: ArrayList<Track> = Gson().fromJson(
        sharedPrefs.getString(KEY_HISTORY_PREFERENCES, ""),
        object : TypeToken<List<Track>>() {}.type
    ) ?: arrayListOf()
    private val adapter = TrackAdapter(trackList()) {}

    companion object {
        const val FILE_HISTORY_PREFERENCES = "history_preferences"
        const val KEY_HISTORY_PREFERENCES = "tracks"
        const val TRACKS_MAX_SIZE = 10
    }

    private fun trackList() = ArrayList(tracks.reversed())

    private fun safe() {
        sharedPrefs.edit()
            .putString(
                KEY_HISTORY_PREFERENCES,
                Gson().toJson(tracks)
            )
            .apply()
    }

    fun add(track: Track) {
        tracks.removeIf { it.trackId == track.trackId }
        if (tracks.size == TRACKS_MAX_SIZE) {
            tracks.removeAt(0)
        }
        tracks.add(track)
        adapter.updateData(trackList())
        safe()
    }

    fun clear() {
        tracks.clear()
        adapter.updateData(trackList())
        sharedPrefs.edit()
            .putString(KEY_HISTORY_PREFERENCES, "")
            .apply()
    }

    fun empty() = tracks.isEmpty()

    fun adapter() = adapter
}