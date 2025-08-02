package com.example.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.interactors.AudioPlayerInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val playerInteractor: AudioPlayerInteractor,
    gson: Gson,
    jsonModel: String
): ViewModel() {

    private val trackModel = gson.fromJson(
        jsonModel,
        Track::class.java
    )

    private val _playerStateLiveData = MutableLiveData<PlayerViewState>(PlayerViewState.Default(trackModel))
    val playerStateLiveData: LiveData<PlayerViewState> = _playerStateLiveData

    fun playerControl() {
        when(_playerStateLiveData.value) {
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

    fun updateTime() {
        val formattedTrackTimeLeft = getFormattedTime()
        val currentState = _playerStateLiveData.value
        _playerStateLiveData.value = when(currentState) {
            is PlayerViewState.Default, is PlayerViewState.Completed, null -> currentState
            is PlayerViewState.Prepared -> currentState.copy(trackTime = formattedTrackTimeLeft)
            is PlayerViewState.Playing -> currentState.copy(trackTime = formattedTrackTimeLeft)
            is PlayerViewState.Paused ->  currentState.copy(trackTime = formattedTrackTimeLeft)
        }
    }

    fun setOnCompletionListenerForPlayer() {
        playerInteractor.setOnCompletionListener {
            _playerStateLiveData.value = PlayerViewState.Completed()
        }
    }

    fun prepareAudioPlayer() {
        playerInteractor.prepare(trackModel.previewUrl)
        _playerStateLiveData.value = PlayerViewState.Prepared(getFormattedTime())
    }

    fun playAudioPlayer() {
        playerInteractor.play()
        _playerStateLiveData.value = PlayerViewState.Playing(getFormattedTime())
    }

    fun pauseAudioPlayer() {
        playerInteractor.pause()
        _playerStateLiveData.value = PlayerViewState.Paused(getFormattedTime())
    }

    fun releaseAudioPlayer() {
        playerInteractor.release()
        _playerStateLiveData.value = PlayerViewState.Default(trackModel)
    }

    private fun getFormattedTime(): String {
        return SimpleDateFormat(
            "m:ss",
            Locale.getDefault()
        ).format(playerInteractor.getCurrentPosition())
    }
}