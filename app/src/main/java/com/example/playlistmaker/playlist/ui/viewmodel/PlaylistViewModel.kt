package com.example.playlistmaker.playlist.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.newplaylist.domain.interactors.PlaylistInteractor
import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewState
import com.google.gson.Gson
import kotlinx.coroutines.launch

class PlaylistViewModel(
    playlistInteractor: PlaylistInteractor,
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
            playlistModel.tracksCount = playlistInteractor.getTracksCountInPlaylist(playlistModel.id ?: 0)
            _state.value = PlaylistViewState.Default(playlistModel)
        }

    }
}