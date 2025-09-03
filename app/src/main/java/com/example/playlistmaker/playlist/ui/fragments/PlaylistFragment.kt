package com.example.playlistmaker.playlist.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.playlist.ui.viewmodel.PlaylistViewModel
import com.example.playlistmaker.playlist.ui.viewmodel.PlaylistViewState
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class PlaylistFragment : Fragment() {

    private val viewModel by viewModel<PlaylistViewModel> {
        parametersOf(requireArguments().getString(ARGS_PLAYLIST))
    }
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

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
        //TODO("Not yet implemented")
    }

    private fun observeLiveData() {
        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            when(state) {
                is PlaylistViewState.Default -> {
                    binding.apply {
                        playlistName.text = state.model.title
                        playlistDescription.text = state.model.description
                        playlistTime.text = "999 минут" /* TODO (Понять как считать) */
                        playlistTracksCount.text = resources.getQuantityString(
                            R.plurals.tracks_count,
                            state.model.tracksCount,
                            state.model.tracksCount
                        )

                            state.model.tracksCount.toString()

                        val file = File(context?.filesDir, state.model.picturePath)
                        Glide.with(playlistCover)
                            .load(file)
                            .placeholder(R.drawable.ic_playlist_placeholder)
                            .error(R.drawable.ic_playlist_placeholder)
                            .centerCrop()
                            .into(playlistCover)
                    }
                }
            }
        }
    }

    private fun setListeners() {
        binding.playlistButtonBack.setOnClickListener {
            findNavController().navigateUp()
        }
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