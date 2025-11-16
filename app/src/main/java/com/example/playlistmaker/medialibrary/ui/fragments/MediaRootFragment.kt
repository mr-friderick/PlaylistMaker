package com.example.playlistmaker.medialibrary.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.medialibrary.ui.compose.MediaRootScreen
import com.example.playlistmaker.medialibrary.ui.viewmodel.FavoritesTracksViewModel
import com.example.playlistmaker.medialibrary.ui.viewmodel.ListPlaylistViewModel
import com.example.playlistmaker.player.ui.fragments.PlayerFragment
import com.example.playlistmaker.playlist.ui.fragments.PlaylistFragment
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class MediaRootFragment : Fragment() {

    private lateinit var composeView: ComposeView
    private val favoritesTracksViewModel by viewModel<FavoritesTracksViewModel>()
    private val listPlaylistViewModel by viewModel<ListPlaylistViewModel>()
    private val gson = Gson()
    private val clickDebounceDelay = 1000L
    private var isClickAllowed = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        composeView = ComposeView(requireContext())
        composeView.setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
        )
        return composeView.apply {
            setContent {
                MediaRootScreen(
                    favoritesTracksViewModel = favoritesTracksViewModel,
                    listPlaylistViewModel = listPlaylistViewModel,
                    openPlayer = { track ->
                        if (clickDebounce()) {
                            findNavController().navigate(
                                R.id.action_mediaRootFragment_to_playerFragment,
                                PlayerFragment.createArgs(gson.toJson(track))
                            )
                        }
                    },
                    openPlaylist = { playlist ->
                        if (clickDebounce()) {
                            findNavController().navigate(
                                R.id.action_mediaRootFragment_to_playlistFragment,
                                PlaylistFragment.createArgs(playlist.id)
                            )
                        }
                    },
                    createPlaylist = {
                        findNavController().navigate(
                            R.id.action_mediaRootFragment_to_newPlaylistFragment
                        )
                    }
                )
            }
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
}