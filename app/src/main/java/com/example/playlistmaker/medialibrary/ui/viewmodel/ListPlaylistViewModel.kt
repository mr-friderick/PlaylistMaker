package com.example.playlistmaker.medialibrary.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.newplaylist.domain.interactors.PlaylistInteractor
import kotlinx.coroutines.launch

class ListPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _state = MutableLiveData<ListPlaylistsViewState>()
    val stateLiveData = _state

    fun setContent() {
        viewModelScope.launch {
            playlistInteractor.getAll()
                .collect { playlists ->
                    if (playlists.isEmpty()) {
                        _state.postValue(ListPlaylistsViewState.Empty)
                    } else {
                        _state.postValue(ListPlaylistsViewState.Content(playlists))
                    }
                }
        }
    }
}