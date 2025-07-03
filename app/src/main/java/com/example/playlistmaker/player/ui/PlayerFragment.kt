package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewState
import com.example.playlistmaker.search.ui.fragments.SearchFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment: Fragment() {

    private lateinit var binding: FragmentPlayerBinding
    private val viewModel by viewModel<PlayerViewModel> {
        parametersOf(requireArguments().getString(SearchFragment.INTENT_EXTRA_TRACK)!!)
    }
    private lateinit var mainHandler: Handler
    private lateinit var timerRunnable: Runnable

    override fun onPause() {
        super.onPause()
        viewModel.pauseAudioPlayer()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainHandler.removeCallbacks(timerRunnable)
        viewModel.releaseAudioPlayer()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initVariables()
        observeLiveData()
        setListeners()
        preparePlayer()
    }

    private fun initVariables() {
        mainHandler = Handler(Looper.getMainLooper())
        timerRunnable = Runnable {
            viewModel.updateTime()
        }
    }

    private fun observeLiveData() {
        viewModel.playerStateLiveData.observe(viewLifecycleOwner) { state ->
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
            // TODO - заменить на навигацию фрагментов
//            startActivity(Intent(this, SearchActivity::class.java))
//            finish()
        }

        binding.buttonPlay.setOnClickListener {
            viewModel.playerControl()
        }
    }

    private fun preparePlayer() {
        mainHandler.postDelayed( {
            viewModel.prepareAudioPlayer()
            viewModel.setOnCompletionListenerForPlayer()
        },
            PREPARE_DELAY
        )
    }

    companion object {
        private const val PREPARE_DELAY = 200L
        private const val TIME_LEFT_DELAY = 300L
    }
}