package com.example.playlistmaker.playlist.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.newplaylist.domain.interactors.PlaylistInteractor
import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val gson: Gson,
    private val playlistId: Int
) : ViewModel() {

    private val _state = MutableLiveData<PlaylistViewState>()
    val stateLiveData: LiveData<PlaylistViewState> = _state
    private var cashTracksList: List<Track> = emptyList()
    private lateinit var playlistModel: Playlist

    private suspend fun initialization() {
        playlistModel = playlistInteractor.getPlaylist(playlistId)
        playlistModel.tracksCount = playlistInteractor.getTracksCountInPlaylist(playlistModel.id)
        playlistInteractor.getTracksForPlaylist(playlistModel.id)
            .collect { tracks ->
                if (tracks.isEmpty()) {
                    _state.value = PlaylistViewState.Default(playlistModel, emptyList(), "0")
                } else {
                    val totalTime = tracks.sumOf { it.trackTimeToMillis() }
                    val totalTimeToString = playlistTimeToString(totalTime)
                    _state.value = PlaylistViewState.Default(playlistModel, tracks, totalTimeToString)
                }
                cashTracksList = tracks
            }
    }

    private fun playlistTimeToString(playlistTime: Long): String {
        return kotlin.runCatching {
            SimpleDateFormat("mm", Locale.getDefault())
                .format(Date(playlistTime))
                .removePrefix("0")
        }.getOrDefault("0")
    }

    private fun tracksInLine(): String {
        var returnValue = ""
        for ((index, value) in cashTracksList.withIndex()) {
            returnValue = returnValue + "\n${index + 1}.${value.artistName} - ${value.trackName} (${value.trackTimeMillis})"
        }
        return returnValue
    }

    fun setDefaultState() {
        viewModelScope.launch {
            initialization()
        }
    }

    fun deleteTrack(trackId: Int) {
        viewModelScope.launch {
            playlistInteractor.deleteTrackFromPlaylist(playlistModel.id, trackId)
            initialization()
        }
    }

    fun modelToGson(): String {
        return gson.toJson(playlistModel)
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlistModel.id)
        }
    }

    fun messageForShare(tracksCount: String): String {
        val message = playlistModel.title + "\n" + playlistModel.description + "\n" + tracksCount + "\n" + tracksInLine()
        return message
    }

    fun dataForMenu() : Map<String, Any> {
        return mapOf(
            "title" to playlistModel.title,
            "trackCount" to playlistModel.tracksCount,
            "coverPath" to playlistModel.picturePath
        )
    }
}