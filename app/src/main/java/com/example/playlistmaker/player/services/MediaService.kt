package com.example.playlistmaker.player.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.player.services.MediaServiceClient
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

class MediaService() : Service(), MediaServiceClient {

    private val mediaPlayerFactory: MediaPlayerFactory by inject()
    private val binder = MediaServiceBinder()
    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Default())
    private val playerState = _playerState.asStateFlow()
    private var mediaPlayer: MediaPlayer? = null
    private var songUrl = ""
    private var artistName = ""
    private var trackName = ""
    private var isRelease = false
    private var currentPosition = 0
    private var timerJob: Job? = null

    // ------------ Override методы Service ------------

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = mediaPlayerFactory.create()
        createNotificationChannel()
    }

    override fun onDestroy() {
        release()
        closeNotification()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        songUrl = intent?.getStringExtra(INTENT_SONG_NAME) ?: ""
        artistName = intent?.getStringExtra(INTENT_ARTIST_NAME) ?: ""
        trackName = intent?.getStringExtra(INTENT_TRACK_NAME) ?: ""
        prepare("")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        release()
        closeNotification()
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
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
            closeNotification()
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

        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    override fun getCurrentPosition(): Int {
        return max(currentPosition, mediaPlayer?.currentPosition ?: 0)
    }

    override fun observePlayerState(): StateFlow<PlayerState> {
        return playerState
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    override fun showNotification() {
        ServiceCompat.startForeground(
            this,
            999,
            createServiceNotification(),
            getForegroundServiceTypeConstant()
        )
    }

    override fun closeNotification() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Music service",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Service for playing music"
            setSound(null, null)
            enableVibration(false)
            enableLights(false)
            setShowBadge(false)
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createServiceNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Playlist Maker")
            .setContentText("$artistName - $trackName")
            .setSmallIcon(R.drawable.ic_button_play)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun getForegroundServiceTypeConstant(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            0
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
        const val INTENT_SONG_NAME = "song_url"
        const val INTENT_ARTIST_NAME = "artist_name"
        const val INTENT_TRACK_NAME = "track_name"
        const val NOTIFICATION_CHANNEL_ID = "music_channel"
        private const val TIME_LEFT_DELAY = 300L
    }
}