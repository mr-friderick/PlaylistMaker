package com.example.playlistmaker.medialibrary.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.medialibrary.domain.interactors.FavoriteTracksInteractor
import kotlinx.coroutines.launch

class FavoritesTracksViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor
): ViewModel() {

    private val _state = MutableLiveData<FavoriteTracksViewState>()
    val stateLiveData = _state

    fun setContent() {
       viewModelScope.launch {
           favoriteTracksInteractor.getAll()
               .collect { tracks ->
                    if (tracks.isEmpty()) {
                        _state.postValue(FavoriteTracksViewState.Empty)
                    } else {
                        _state.postValue(FavoriteTracksViewState.Content(tracks))
                    }
               }
       }
    }

}