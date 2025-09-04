package com.example.playlistmaker.playlist.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.newplaylist.domain.interactors.PlaylistInteractor
import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    gson: Gson,
    jsonModel: String
) : ViewModel() {

    private val playlistModel = gson.fromJson(
        jsonModel,
        Playlist::class.java
    )
    private val _state = MutableLiveData<PlaylistViewState>()
    val stateLiveData: LiveData<PlaylistViewState> = _state

    init {
        viewModelScope.launch {
            initialization()
        }
    }

    private suspend fun initialization() {
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
            }
    }

    private fun playlistTimeToString(playlistTime: Long): String {
        return kotlin.runCatching {
            SimpleDateFormat("mm", Locale.getDefault())
                .format(Date(playlistTime))
                .removePrefix("0")
        }.getOrDefault("0")
    }

    fun deleteTrack(trackId: Int) {
        viewModelScope.launch {
            playlistInteractor.deleteTrackFromPlaylist(playlistModel.id, trackId)
            initialization()
        }
    }
}