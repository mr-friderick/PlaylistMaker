package com.example.playlistmaker.medialibrary.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentListPlaylistBinding
import com.example.playlistmaker.medialibrary.ui.adapter.PlaylistsAdapter
import com.example.playlistmaker.medialibrary.ui.viewmodel.ListPlaylistViewModel
import com.example.playlistmaker.medialibrary.ui.viewmodel.ListPlaylistsViewState
import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.example.playlistmaker.playlist.ui.fragments.PlaylistFragment
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListPlaylistFragment : Fragment() {

    private val clickDebounceDelay = 1000L
    private var _binding: FragmentListPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<ListPlaylistViewModel>()
    private lateinit var playlistsAdapter: PlaylistsAdapter
    private var isClickAllowed = true
    private  val gson = Gson()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initVariables()
        observeLiveData()
        setListeners()
        viewModel.setContent()
    }

    private fun initVariables() {
        // Инициализация переменных
    }

    private fun observeLiveData() {
        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ListPlaylistsViewState.Content -> {
                    playlistsAdapter = PlaylistsAdapter(
                        R.layout.element_playlist,
                        state.playlists
                    ) { playlist -> startPlaylistFragment(playlist)}

                    binding.playlistRecyclerView.layoutManager =
                        GridLayoutManager(requireContext(), 2)
                    binding.playlistRecyclerView.adapter = playlistsAdapter
                    binding.playlistPlaceholder.isVisible = false
                    binding.playlistRecyclerView.isVisible = true
                }

                is ListPlaylistsViewState.Empty -> {
                    binding.playlistPlaceholder.isVisible = true
                    binding.playlistRecyclerView.isVisible = false
                }
            }
        }
    }

    private fun setListeners() {
        binding.buttonNewPlaylist.setOnClickListener {
            findNavController().navigate(
                R.id.action_mediaRootFragment_to_newPlaylistFragment
            )
        }
    }

    private fun startPlaylistFragment(playlist: Playlist) {
        if (clickDebounce()) {
            findNavController().navigate(
                R.id.action_mediaRootFragment_to_playlistFragment,
//                PlaylistFragment.createArgs(gson.toJson(playlist))
                PlaylistFragment.createArgs(playlist.id)
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
        fun newInstance() = ListPlaylistFragment().apply {
            arguments = Bundle().apply {
                // TODO (Реализация будет в будущем)
            }
        }
    }
}