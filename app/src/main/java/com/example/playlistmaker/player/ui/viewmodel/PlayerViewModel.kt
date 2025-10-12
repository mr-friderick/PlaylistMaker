package com.example.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.medialibrary.domain.interactors.FavoriteTracksInteractor
import com.example.playlistmaker.newplaylist.domain.interactors.PlaylistInteractor
import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.example.playlistmaker.player.services.MediaServiceClient
import com.example.playlistmaker.player.services.PlayerState
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistInteractor: PlaylistInteractor,
    gson: Gson,
    jsonModel: String
) : ViewModel() {

    private val trackModel = gson.fromJson(
        jsonModel,
        Track::class.java
    )
    private var isFavorite = false
    private var mediaServiceClient: MediaServiceClient? = null
    private val _state = MutableLiveData<PlayerViewState>()
    val stateLiveData: LiveData<PlayerViewState> = _state

    init {
        viewModelScope.launch {
            isFavorite = favoriteTracksInteractor.isFavorite(trackModel.trackId)
            _state.value = PlayerViewState.Default(isFavorite, trackModel)
        }
    }

    private fun getFormattedTime(): String {
        return SimpleDateFormat(
            "m:ss",
            Locale.getDefault()
        ).format(mediaServiceClient?.getCurrentPosition())
    }

    fun setAudioPlayerClient(mediaServiceClient: MediaServiceClient) {
        this.mediaServiceClient = mediaServiceClient

        viewModelScope.launch {
            mediaServiceClient.observePlayerState().collect {
                _state.value =
                    when(it) {
                        is PlayerState.Default -> PlayerViewState.Default(isFavorite, trackModel)
                        is PlayerState.Paused -> PlayerViewState.Paused(isFavorite, it.trackTime)
                        is PlayerState.Playing -> PlayerViewState.Playing(isFavorite, it.trackTime)
                        is PlayerState.Prepared -> PlayerViewState.Prepared(isFavorite, it.trackTime)
                        is PlayerState.Complete -> PlayerViewState.Completed(isFavorite)
                    }
            }
        }
    }

    fun removeAudioPlayerClient() {
        mediaServiceClient = null
    }

    fun playerControl() {
        when (_state.value) {
            is PlayerViewState.Playing -> {
                mediaServiceClient?.pause()
            }
            is PlayerViewState.Default, is PlayerViewState.Prepared, is PlayerViewState.Paused, is PlayerViewState.Completed, null -> {
                mediaServiceClient?.play()
            }
            is PlayerViewState.Playlists, is PlayerViewState.ResultAddTrack -> {}
        }
    }

    fun favoriteControl() {
        viewModelScope.launch {
            if (isFavorite) {
                isFavorite = false
                favoriteTracksInteractor.deleteTrack(trackModel.trackId)
            } else {
                isFavorite = true
                favoriteTracksInteractor.addTrack(trackModel)
            }
            val currentState = _state.value
            _state.value = when (currentState) {
                is PlayerViewState.Completed -> currentState.copy(isFavorite)
                is PlayerViewState.Default -> currentState.copy(isFavorite, trackModel)
                is PlayerViewState.Paused -> currentState.copy(isFavorite, getFormattedTime())
                is PlayerViewState.Playing -> currentState.copy(isFavorite, getFormattedTime())
                is PlayerViewState.Prepared -> currentState.copy(isFavorite, getFormattedTime())
                else -> {
                    PlayerViewState.Default(isFavorite, trackModel)
                }
            }
        }
    }

    fun playlistsControl() {
        viewModelScope.launch {
            playlistInteractor.getAll()
                .collect { playlists ->
                    _state.value = PlayerViewState.Playlists(playlists)
                }
        }
    }

    fun addTrackInPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            val alreadyAdd = playlistInteractor.isTrackInPlaylist(playlist.id!!, trackModel.trackId)
            if (alreadyAdd) {
                _state.value = PlayerViewState.ResultAddTrack(
                    false,
                    R.string.playlist_track_allready_add,
                    playlist.title
                )
            } else {
                playlistInteractor.addPlaylistTrack(trackModel)
                playlistInteractor.addTrackToPlaylist(playlist.id, trackModel.trackId)

                _state.value =
                    PlayerViewState.ResultAddTrack(
                        true,
                        R.string.playlist_track_success_add,
                        playlist.title
                    )

            }
        }
    }

    fun showNotification() {
        if (mediaServiceClient?.isPlaying() == true) {
            mediaServiceClient?.showNotification()
        }
    }
    
    fun closeNotification() {
        mediaServiceClient?.closeNotification()
    }

    fun getSongUrl(): String {
        return trackModel.previewUrl
    }

    fun getArtistName(): String {
        return trackModel.artistName
    }

    fun getTrackName(): String {
        return trackModel.trackName
    }
}