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
import com.example.playlistmaker.player.ui.view_model.PlayerCommand
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
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

        viewModel.setTrack()
        viewModel.setOnCompletionListenerForPlayer()

        mainHandler = Handler(Looper.getMainLooper())
        timerRunnable = Runnable {
            viewModel.updateTime()
            if (viewModel.playerStateLiveData.value == PlayerViewModel.STATE_PLAYING) {
                mainHandler.postDelayed(timerRunnable, TIME_LEFT_DELAY)
            }
        }
    }

    private fun observeLiveData() {
        viewModel.playerStateLiveData.observe(this) { state ->
            when(state) {
                PlayerViewModel.STATE_DEFAULT -> {
                    binding.buttonPlay.isEnabled = false
                    binding.buttonPlay.setImageResource(R.drawable.ic_button_play)
                }
                PlayerViewModel.STATE_PREPARED -> {
                    binding.buttonPlay.isEnabled = true
                    binding.buttonPlay.setImageResource(R.drawable.ic_button_play)
                    binding.trackTimeLeft.text = getResources().getString(R.string.default_time_left);
                }
                PlayerViewModel.STATE_PLAYING -> {
                    binding.buttonPlay.setImageResource(R.drawable.ic_button_pause)
                }
                PlayerViewModel.STATE_PAUSED -> {
                    binding.buttonPlay.setImageResource(R.drawable.ic_button_play)
                }
            }
        }

        viewModel.trackLiveData.observe(this) { track ->
            binding.trackName.text = track.trackName
            binding.trackArtist.text = track.artistName
            binding.trackTime.text = track.trackTimeMillis
            binding.trackCollection.text = track.collectionName
            binding.trackRelease.text = track.getReleaseYear()
            binding.trackGenre.text = track.primaryGenreName
            binding.trackCountry.text = track.country

            Glide.with(binding.trackPoster)
                .load(track.getCoverArtwork())
                .placeholder(R.drawable.ic_track_placeholder)
                .into(binding.trackPoster)
        }

        viewModel.trackTimeLeftLiveData.observe(this) { value ->
            binding.trackTimeLeft.text = value
        }

        viewModel.timerCommandLiveData.observe(this) { command ->
            when(command) {
                PlayerCommand.StartTimer -> mainHandler.post(timerRunnable)
                PlayerCommand.StopTimer -> mainHandler.removeCallbacks(timerRunnable)
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
        viewModel.prepareAudioPlayer()
    }

    companion object {
        private const val TIME_LEFT_DELAY = 300L
    }
}