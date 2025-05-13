package com.example.playlistmaker.ui.player

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.search.SearchActivity
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity:  AppCompatActivity() {
    private var playerState = STATE_DEFAULT

    private lateinit var trackModel: Track
    private lateinit var buttonBack: ImageButton
    private lateinit var buttonPlay: ImageButton
    private lateinit var trackPoster: ShapeableImageView
    private lateinit var trackName: MaterialTextView
    private lateinit var trackArtist: MaterialTextView
    private lateinit var trackTime: MaterialTextView
    private lateinit var trackTimeLeft: MaterialTextView
    private lateinit var trackCollection: MaterialTextView
    private lateinit var trackRelease: MaterialTextView
    private lateinit var trackGenre: MaterialTextView
    private lateinit var trackCountry: MaterialTextView

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mainHandler: Handler
    private lateinit var playRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_player)
        setupWindowInsets()

        initVariables()
        setListeners()
        preparePlayer()
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainHandler.removeCallbacks(playRunnable)
        mediaPlayer.release()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.screen_player)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initVariables() {
        trackModel = Gson().fromJson(
            intent.getStringExtra(SearchActivity.INTENT_EXTRA_TRACK),
            Track::class.java
        )

        buttonBack = findViewById(R.id.player_back)
        buttonPlay = findViewById(R.id.button_play)
        trackPoster = findViewById(R.id.track_poster)
        trackName = findViewById(R.id.track_name)
        trackArtist = findViewById(R.id.track_artist)
        trackTime = findViewById(R.id.track_time)
        trackTimeLeft = findViewById(R.id.track_time_left)
        trackCollection = findViewById(R.id.track_collection)
        trackRelease = findViewById(R.id.track_release)
        trackGenre = findViewById(R.id.track_genre)
        trackCountry = findViewById(R.id.track_country)

        Glide.with(trackPoster)
            .load(trackModel.getCoverArtwork())
            .placeholder(R.drawable.ic_track_placeholder)
            .into(trackPoster)

        trackName.text = trackModel.trackName
        trackArtist.text = trackModel.artistName
        trackTime.text = trackModel.trackTimeMillis
        trackCollection.text = trackModel.collectionName
        trackRelease.text = trackModel.getReleaseYear()
        trackGenre.text = trackModel.primaryGenreName
        trackCountry.text = trackModel.country

        mainHandler = Handler(Looper.getMainLooper())
        playRunnable = Runnable {
            trackTimeLeft.text = SimpleDateFormat("m:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
            if (playerState == STATE_PLAYING) {
                mainHandler.postDelayed(playRunnable, TIME_LEFT_DELAY)
            }
        }
    }

    private fun setListeners() {
        buttonBack.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
            finish()
        }

        buttonPlay.setOnClickListener {
            playbackControl()
        }
    }

    private fun preparePlayer() {
        mediaPlayer = MediaPlayer()

        mediaPlayer.setDataSource(trackModel.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
            buttonPlay.isEnabled = true
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            buttonPlay.setImageResource(R.drawable.ic_button_play)
            mainHandler.removeCallbacks(playRunnable)

            trackTimeLeft.text = getResources().getString(R.string.default_time_left);
        }
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        buttonPlay.setImageResource(R.drawable.ic_button_pause)
        playerState = STATE_PLAYING

        mainHandler.post(playRunnable)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        buttonPlay.setImageResource(R.drawable.ic_button_play)
        playerState = STATE_PAUSED

        mainHandler.removeCallbacks(playRunnable)
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val TIME_LEFT_DELAY = 300L
    }
}