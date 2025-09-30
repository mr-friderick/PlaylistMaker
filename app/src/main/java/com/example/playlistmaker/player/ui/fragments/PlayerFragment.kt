package com.example.playlistmaker.player.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.medialibrary.ui.adapter.PlaylistsAdapter
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {

    private val viewModel by viewModel<PlayerViewModel> {
        parametersOf(requireArguments().getString(ARGS_TRACK))
    }
    private lateinit var binding: FragmentPlayerBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var playlistsAdapter: PlaylistsAdapter

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
        setBottomSheet(BottomSheetBehavior.STATE_HIDDEN)
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
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetInclude.root)
    }

    private fun observeLiveData() {
        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlayerViewState.Default -> {
                    binding.buttonPlay.isEnabled = false
                    binding.buttonPlay.isPlaying = true

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
                    binding.buttonPlay.isPlaying = true
                    binding.trackTimeLeft.text = state.trackTime;

                    setFavoriteIcon(state.trackIsFavorite)
                }

                is PlayerViewState.Playing -> {
                    binding.buttonPlay.isPlaying = false
                    binding.trackTimeLeft.text = state.trackTime;

                    setFavoriteIcon(state.trackIsFavorite)
                }

                is PlayerViewState.Paused -> {
                    binding.buttonPlay.isPlaying = true
                    binding.trackTimeLeft.text = state.trackTime;

                    setFavoriteIcon(state.trackIsFavorite)
                }

                is PlayerViewState.Completed -> {
                    binding.buttonPlay.isPlaying = true
                    binding.trackTimeLeft.text = state.trackTime;

                    setFavoriteIcon(state.trackIsFavorite)
                }

                is PlayerViewState.Playlists -> {
                    playlistsAdapter = PlaylistsAdapter(
                        R.layout.element_playlist_for_player,
                        state.playlists
                    ) { playlist -> viewModel.addTrackInPlaylist(playlist) }

                    binding.bottomSheetInclude.playlistRecyclerView.adapter = playlistsAdapter

                    setBottomSheet(BottomSheetBehavior.STATE_COLLAPSED)
                    binding.overlay.isVisible = true
                }

                is PlayerViewState.ResultAddTrack -> {
                    if (state.success) {
                        setBottomSheet(BottomSheetBehavior.STATE_HIDDEN)
                    }
                    Toast.makeText(requireContext(),
                        getString(state.messageId, state.playlistTitle),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setListeners() {
        binding.playerBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonPlay.clickEventListener = {
            viewModel.playerControl()
        }

        binding.buttonAddFavorite.setOnClickListener {
            viewModel.favoriteControl()
        }

        binding.buttonAddPlaylist.setOnClickListener {
            viewModel.playlistsControl()
        }

        binding.bottomSheetInclude.buttonNewPlaylist.setOnClickListener {
            findNavController().navigate(
                R.id.action_playerFragment_to_newPlaylistFragment
            )
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN-> {
                        binding.overlay.isVisible = false
                    }
                    else -> {
                        binding.overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
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

    private fun setBottomSheet(state: Int) {
        bottomSheetBehavior.state = state
    }

    companion object {
        const val ARGS_TRACK = "track"

        fun createArgs(jsonTrack: String): Bundle {
            return bundleOf(ARGS_TRACK to jsonTrack)
        }
    }
}

