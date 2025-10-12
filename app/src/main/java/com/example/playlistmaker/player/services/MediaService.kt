package com.example.playlistmaker.player.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.example.playlistmaker.player.data.AudioPlayerClient
import com.example.playlistmaker.player.data.MediaPlayerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.max

class MediaService() : Service(), AudioPlayerClient {

    private val mediaPlayerFactory: MediaPlayerFactory by inject()
    private val binder = MediaServiceBinder()
    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Default())
    private val playerState = _playerState.asStateFlow()
    private var mediaPlayer: MediaPlayer? = null
    private var songUrl = ""
    private var isRelease = false
    private var currentPosition = 0
    private var timerJob: Job? = null

    // ------------ Override методы Service ------------

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = mediaPlayerFactory.create()
    }

    override fun onDestroy() {
        release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        // TODO Этот код нужен, если сервис запущен как bound-service
        songUrl = intent?.getStringExtra(INTENT_NAME) ?: ""
        prepare("")
        return binder
    }

    // ------------ Override методы AudioPlayerClient ------------

    override fun prepare(url: String) {
        if (isRelease) {
            mediaPlayer = mediaPlayerFactory.create()
            isRelease = false
        }
        mediaPlayer?.apply {
            setDataSource(songUrl)
            prepareAsync()
        }
        mediaPlayer?.setOnCompletionListener {
            timerJob?.cancel()
            currentPosition = 0;
            _playerState.value = PlayerState.Complete()
        }
    }

    override fun play() {
        mediaPlayer?.seekTo(currentPosition)
        mediaPlayer?.start()
        _playerState.value = PlayerState.Playing(getFormattedTime())
        startTimer()
    }

    override fun pause() {
        currentPosition = getCurrentPosition()
        mediaPlayer?.pause()
        timerJob?.cancel()
        _playerState.value = PlayerState.Paused(getFormattedTime())
    }

    override fun release() {
        timerJob?.cancel()
        currentPosition = getCurrentPosition();
        mediaPlayer?.stop()
        mediaPlayer?.setOnPreparedListener(null)
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer?.release()
        mediaPlayer = null

        isRelease = true
    }

    override fun getCurrentPosition(): Int {
        return max(currentPosition, mediaPlayer?.currentPosition ?: 0)
    }

    override fun observePlayerState(): StateFlow<PlayerState> {
        return playerState
    }

    // ------------ Приватные методы ------------
    private fun startTimer() {
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (mediaPlayer?.isPlaying == true) {
                delay(TIME_LEFT_DELAY)
                _playerState.value = PlayerState.Playing(getFormattedTime())
            }
        }
    }

    fun getFormattedTime(): String {
        return SimpleDateFormat(
            "m:ss",
            Locale.getDefault()
        ).format(getCurrentPosition())
    }

    // ------------ Внутренний класс ------------

    inner class MediaServiceBinder : Binder() {
        fun getService(): MediaService = this@MediaService
    }

    companion object {
        const val INTENT_NAME = "song_url"
        private const val TIME_LEFT_DELAY = 300L
    }
}

//Но если сервис изначально запущен как foreground,
//то наверняка понадобится продолжать воспроизведение даже тогда,
//когда от него отвязались все клиенты.
//В этом случае releasePlayer() лучше вызывать в методе onDestroy(): здесь мы знаем точно, что система уничтожает сервис.
//Подробнее о жизненном цикле сервиса мы поговорим в следующем уроке.
//Если вы хотите, чтобы во время работы привязанного сервиса отображалось уведомление, то это возможно.
//Для этого действуем, как и в случае с foreground-сервисом, но с двумя отличиями: