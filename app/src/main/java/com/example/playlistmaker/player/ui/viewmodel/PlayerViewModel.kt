package com.example.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.medialibrary.domain.interactors.FavoriteTracksInteractor
import com.example.playlistmaker.player.domain.interactors.AudioPlayerInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val playerInteractor: AudioPlayerInteractor,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    gson: Gson,
    jsonModel: String
): ViewModel() {

    private val trackModel = gson.fromJson(
        jsonModel,
        Track::class.java
    )
    private var isFavorite = false
    private var timerJob: Job? = null
    private val _state = MutableLiveData<PlayerViewState>()
    val stateLiveData: LiveData<PlayerViewState> = _state

    init {
        viewModelScope.launch {
            isFavorite = favoriteTracksInteractor.isFavorite(trackModel.trackId)
            _state.value = PlayerViewState.Default(isFavorite, trackModel)
        }
    }

    private fun setOnCompletionListenerForPlayer() {
        playerInteractor.setOnCompletionListener {
            timerJob?.cancel()
            _state.value = PlayerViewState.Completed(isFavorite)
        }
    }

    private fun getFormattedTime(): String {
        return SimpleDateFormat(
            "m:ss",
            Locale.getDefault()
        ).format(playerInteractor.getCurrentPosition())
    }

    fun playerControl() {
        when(_state.value) {
            is PlayerViewState.Default, null -> {
                prepareAudioPlayer()
            }

            is PlayerViewState.Playing -> {
                pauseAudioPlayer()
            }
            is PlayerViewState.Prepared, is PlayerViewState.Paused, is PlayerViewState.Completed -> {
                playAudioPlayer()
            }
        }
    }

    fun prepareAudioPlayer() {
        viewModelScope.launch {
            delay(PREPARE_DELAY)
            playerInteractor.prepare(trackModel.previewUrl)
            _state.value = PlayerViewState.Prepared(isFavorite,getFormattedTime())

            setOnCompletionListenerForPlayer()
        }
    }

    fun playAudioPlayer() {
        playerInteractor.play()
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                delay(TIME_LEFT_DELAY)
                _state.value = PlayerViewState.Playing(isFavorite, getFormattedTime())
            }
        }
    }

    fun pauseAudioPlayer() {
        playerInteractor.pause()
        timerJob?.cancel()
        _state.value = PlayerViewState.Paused(isFavorite, getFormattedTime())
    }

    fun releaseAudioPlayer() {
        playerInteractor.release()
        timerJob?.cancel()
        _state.value = PlayerViewState.Default(isFavorite, trackModel)
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
                else -> { PlayerViewState.Default(isFavorite, trackModel) }
            }
        }
    }

    companion object {
        private const val PREPARE_DELAY = 200L
        private const val TIME_LEFT_DELAY = 300L
    }
}