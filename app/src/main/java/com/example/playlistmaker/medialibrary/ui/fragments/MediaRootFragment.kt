package com.example.playlistmaker.medialibrary.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.playlistmaker.medialibrary.ui.compose.MediaRootScreen
import com.example.playlistmaker.medialibrary.ui.viewmodel.FavoritesTracksViewModel
import com.example.playlistmaker.medialibrary.ui.viewmodel.ListPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class MediaRootFragment : Fragment() {

    private val favoritesTracksViewModel by viewModel<FavoritesTracksViewModel>()
    private val listPlaylistViewModel by viewModel<ListPlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                MediaRootScreen(
                    favoritesTracksViewModel = favoritesTracksViewModel,
                    listPlaylistViewModel = listPlaylistViewModel
                )
            }
        }
    }
}