package com.example.playlistmaker.player.ui.fragments

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.medialibrary.ui.adapter.PlaylistsAdapter
import com.example.playlistmaker.player.services.MediaService
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
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MediaService.MediaServiceBinder
            viewModel.setAudioPlayerClient(binder.getService())
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            viewModel.removeAudioPlayerClient()
        }
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startMediaServiceForeground()
        } else {
            // Иначе просто покажем ошибку
            Toast.makeText(requireContext(), "Can't start foreground service!", Toast.LENGTH_LONG).show()
        }
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
        bindMediaService()
        setBottomSheet(BottomSheetBehavior.STATE_HIDDEN)
    }

    override fun onDestroyView() {
        unbindMusicService()
        super.onDestroyView()
    }

    override fun onResume() {
        viewModel.closeNotification()
        super.onResume()
    }

    override fun onStop() {
        viewModel.showNotification()
        super.onStop()
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
            launchPermissionAndStartMediaServiceForeground()
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

    private fun bindMediaService() {
        val intent = Intent(requireContext(), MediaService::class.java).apply {
            putExtra(MediaService.INTENT_SONG_NAME, viewModel.getSongUrl())
            putExtra(MediaService.INTENT_ARTIST_NAME, viewModel.getArtistName())
            putExtra(MediaService.INTENT_TRACK_NAME, viewModel.getTrackName())
        }
        requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindMusicService() {
        requireContext().unbindService(serviceConnection)
    }

    private fun startMediaServiceForeground() {
        val intent = Intent(requireContext(), MediaService::class.java)
        ContextCompat.startForegroundService(requireContext(), intent)
    }

    private fun launchPermissionAndStartMediaServiceForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            startMediaServiceForeground()
        }
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

