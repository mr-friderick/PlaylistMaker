package com.example.playlistmaker.player.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.player.ui.view_model.PlayerViewState
import com.example.playlistmaker.search.ui.activity.SearchActivity

class PlayerActivity:  AppCompatActivity() {
    private lateinit var mainHandler: Handler
    private lateinit var timerRunnable: Runnable

    private lateinit var binding: ActivityAudioPlayerBinding
    private lateinit var viewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        initVariables()

        setContentView(binding.root)
        setupWindowInsets()

        observeLiveData()
        setListeners()

        preparePlayer()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pauseAudioPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainHandler.removeCallbacks(timerRunnable)
        viewModel.releaseAudioPlayer()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.screenPlayer) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initVariables() {
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.getViewModelFactory(
                intent.getStringExtra(SearchActivity.INTENT_EXTRA_TRACK
                )!!
            )
        )[PlayerViewModel::class.java]

        viewModel.setOnCompletionListenerForPlayer()

        mainHandler = Handler(Looper.getMainLooper())
        timerRunnable = Runnable {
            viewModel.updateTime()
        }
    }

    private fun observeLiveData() {
        viewModel.playerStateLiveData.observe(this) { state ->
            when(state) {
                is PlayerViewState.Default -> {
                    binding.buttonPlay.isEnabled = false
                    binding.buttonPlay.setImageResource(R.drawable.ic_button_play)

                    binding.trackName.text = state.trackModel.trackName
                    binding.trackArtist.text = state.trackModel.artistName
                    binding.trackTime.text = state.trackModel.trackTimeMillis
                    binding.trackCollection.text = state.trackModel.collectionName
                    binding.trackRelease.text = state.trackModel.getReleaseYear()
                    binding.trackGenre.text = state.trackModel.primaryGenreName
                    binding.trackCountry.text = state.trackModel.country

                    Glide.with(binding.trackPoster)
                        .load(state.trackModel.getCoverArtwork())
                        .placeholder(R.drawable.ic_track_placeholder)
                        .into(binding.trackPoster)
                }
                is PlayerViewState.Prepared -> {
                    binding.buttonPlay.isEnabled = true
                    binding.buttonPlay.setImageResource(R.drawable.ic_button_play)
                    binding.trackTimeLeft.text = state.trackTime;
                }
                is PlayerViewState.Playing -> {
                    binding.buttonPlay.setImageResource(R.drawable.ic_button_pause)
                    binding.trackTimeLeft.text = state.trackTime;
                    mainHandler.postDelayed(timerRunnable, TIME_LEFT_DELAY)
                }
                is PlayerViewState.Paused -> {
                    binding.buttonPlay.setImageResource(R.drawable.ic_button_play)
                    binding.trackTimeLeft.text = state.trackTime;
                    mainHandler.removeCallbacks(timerRunnable)
                }
                is PlayerViewState.Complite -> {
                    binding.buttonPlay.setImageResource(R.drawable.ic_button_play)
                    binding.trackTimeLeft.text = state.trackTime;
                    mainHandler.removeCallbacks(timerRunnable)
                }
            }
        }
    }

    private fun setListeners() {
        binding.playerBack.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
            finish()
        }

        binding.buttonPlay.setOnClickListener {
            viewModel.playerControl()
        }
    }

    private fun preparePlayer() {
        mainHandler.postDelayed({ viewModel.prepareAudioPlayer() }, PREPARE_DELAY)

    }

    companion object {
        private const val PREPARE_DELAY = 200L
        private const val TIME_LEFT_DELAY = 300L
    }
}