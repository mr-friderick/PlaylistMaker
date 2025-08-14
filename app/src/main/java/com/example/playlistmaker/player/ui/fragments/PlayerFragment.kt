package com.example.playlistmaker.player.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewState
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {

    private lateinit var binding: FragmentPlayerBinding
    private val viewModel by viewModel<PlayerViewModel> {
        parametersOf(requireArguments().getString(ARGS_TRACK))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initVariables()
        observeLiveData()
        setListeners()
        preparePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.releaseAudioPlayer()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pauseAudioPlayer()
    }

    private fun initVariables() {
        // Инициализация переменных
    }

    private fun observeLiveData() {
        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
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

                    setFavoriteIcon(state.trackIsFavorite)

                    Glide.with(binding.trackPoster)
                        .load(state.trackModel.getCoverArtwork())
                        .placeholder(R.drawable.ic_track_placeholder)
                        .into(binding.trackPoster)
                }

                is PlayerViewState.Prepared -> {
                    binding.buttonPlay.isEnabled = true
                    binding.buttonPlay.setImageResource(R.drawable.ic_button_play)
                    binding.trackTimeLeft.text = state.trackTime;

                    setFavoriteIcon(state.trackIsFavorite)
                }

                is PlayerViewState.Playing -> {
                    binding.buttonPlay.setImageResource(R.drawable.ic_button_pause)
                    binding.trackTimeLeft.text = state.trackTime;

                    setFavoriteIcon(state.trackIsFavorite)
                }

                is PlayerViewState.Paused -> {
                    binding.buttonPlay.setImageResource(R.drawable.ic_button_play)
                    binding.trackTimeLeft.text = state.trackTime;

                    setFavoriteIcon(state.trackIsFavorite)
                }

                is PlayerViewState.Completed -> {
                    binding.buttonPlay.setImageResource(R.drawable.ic_button_play)
                    binding.trackTimeLeft.text = state.trackTime;

                    setFavoriteIcon(state.trackIsFavorite)
                }
            }
        }
    }

    private fun setListeners() {
        binding.playerBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonPlay.setOnClickListener {
            viewModel.playerControl()
        }

        binding.buttonAddFavorite.setOnClickListener {
            viewModel.favoriteControl()
        }
    }

    private fun preparePlayer() {
        viewModel.prepareAudioPlayer()
    }

    private fun setFavoriteIcon(favorite: Boolean) {
        binding.buttonAddFavorite.setImageResource(
            if (favorite) {
                R.drawable.ic_button_favorite_active
            } else {
                R.drawable.ic_button_favorite
            }
        )
    }

    companion object {
        const val ARGS_TRACK = "track"

        fun createArgs(jsonTrack: String): Bundle {
            return bundleOf(ARGS_TRACK to jsonTrack)
        }
    }
}

