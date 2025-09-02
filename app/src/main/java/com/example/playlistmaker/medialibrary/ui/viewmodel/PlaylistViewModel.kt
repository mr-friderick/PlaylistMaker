package com.example.playlistmaker.medialibrary.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.newplaylist.domain.interactors.PlaylistInteractor
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _state = MutableLiveData<PlaylistsViewState>()
    val stateLiveData = _state

    fun setContent() {
        viewModelScope.launch {
            playlistInteractor.getAll()
                .collect { playlists ->
                    if (playlists.isEmpty()) {
                        _state.postValue(PlaylistsViewState.Empty)
                    } else {
                        _state.postValue(PlaylistsViewState.Content(playlists))
                    }
                }
        }
    }
}