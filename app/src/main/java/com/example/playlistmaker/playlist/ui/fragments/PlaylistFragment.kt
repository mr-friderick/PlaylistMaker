package com.example.playlistmaker.playlist.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.player.ui.fragments.PlayerFragment
import com.example.playlistmaker.playlist.ui.viewmodel.PlaylistViewModel
import com.example.playlistmaker.playlist.ui.viewmodel.PlaylistViewState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.adapter.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class PlaylistFragment : Fragment() {

    private val clickDebounceDelay = 1000L
    private var isClickAllowed = true
    private  val gson = Gson()
    private val viewModel by viewModel<PlaylistViewModel> {
        parametersOf(requireArguments().getString(ARGS_PLAYLIST))
    }
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private lateinit var tracksAdapter: TrackAdapter
    private lateinit var confirmDialog: MaterialAlertDialogBuilder
    private var removableTrackId: Int = 0
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initVariables()
        observeLiveData()
        setListeners()
    }

    private fun initVariables() {
        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.playlist_longclick_message))
            .setNegativeButton(getString(R.string.playlist_longlick_negative_button)) { dialog, which ->
                // Ничего не делаем
            }
            .setPositiveButton(getString(R.string.playlist_longlick_positive_button)) { dialog, which ->
               viewModel.deleteTrack(removableTrackId)
            }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistBottomSheetInclude.root)
    }

    private fun observeLiveData() {
        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            when(state) {
                is PlaylistViewState.Default -> {
                    binding.apply {
                        playlistName.text = state.model.title
                        playlistDescription.text = state.model.description
                        playlistTracksCount.text = resources.getQuantityString(
                            R.plurals.tracks_count,
                            state.model.tracksCount,
                            state.model.tracksCount
                        )
                        playlistTime.text = resources.getQuantityString(
                            R.plurals.playlist_time,
                            state.playlistTime.toInt(),
                            state.playlistTime
                        )

                        val file = File(context?.filesDir, state.model.picturePath)
                        Glide.with(playlistCover)
                            .load(file)
                            .placeholder(R.drawable.ic_playlist_placeholder)
                            .error(R.drawable.ic_playlist_placeholder)
                            .centerCrop()
                            .into(playlistCover)

                        tracksAdapter = TrackAdapter(
                            tracks = state.tracks.toCollection(ArrayList()),
                            clickItem = { startPlayerFragment(it) },
                            longClickItem = {
                                removableTrackId = it
                                confirmDialog.show()
                            }
                        )
                        binding.playlistBottomSheetInclude.playlistRecyclerView.adapter = tracksAdapter
                    }
                }
            }
        }
    }

    private fun setListeners() {
        binding.playlistButtonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED-> {
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

    private fun startPlayerFragment(track: Track) {
        if (clickDebounce()) {
            findNavController().navigate(
                R.id.action_playlistFragment_to_playerFragment,
                PlayerFragment.createArgs(gson.toJson(track))
            )
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            lifecycleScope.launch {
                delay(clickDebounceDelay)
                isClickAllowed = true
            }
        }
        return current
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val ARGS_PLAYLIST = "playlist"

        fun createArgs(jsonPlaylist: String): Bundle {
            return bundleOf(ARGS_PLAYLIST to jsonPlaylist)
        }
    }
}