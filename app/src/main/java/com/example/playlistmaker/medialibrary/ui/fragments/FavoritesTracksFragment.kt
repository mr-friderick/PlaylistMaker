package com.example.playlistmaker.medialibrary.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesTracksBinding
import com.example.playlistmaker.medialibrary.ui.viewmodel.FavoriteTracksViewState
import com.example.playlistmaker.medialibrary.ui.viewmodel.FavoritesTracksViewModel
import com.example.playlistmaker.player.ui.fragments.PlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.adapter.TrackAdapter
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesTracksFragment : Fragment() {

    private val clickDebounceDelay = 1000L
    private var _binding: FragmentFavoritesTracksBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<FavoritesTracksViewModel>()
    private var isClickAllowed = true
    private lateinit var historyAdapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesTracksBinding.inflate(inflater, container, false)
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
                is FavoriteTracksViewState.Content -> {
                    historyAdapter = TrackAdapter(state.favoriteTracks.toCollection(ArrayList())) { track ->
                        startPlayerFragment(track)
                    }
                    binding.favoriteRecyclerView.adapter = historyAdapter
                    binding.favoritePlaceholder.isVisible = false
                    binding.favoriteRecyclerView.isVisible = true
                }
                is FavoriteTracksViewState.Empty -> {
                    binding.favoritePlaceholder.isVisible = true
                    binding.favoriteRecyclerView.isVisible = false
                }
            }
        }
    }

    private fun setListeners() {
        // Инициализация слушателей
    }

    private fun startPlayerFragment(track: Track) {
        if (clickDebounce()) {
            findNavController().navigate(
                R.id.action_mediaRootFragment_to_playerFragment,
                PlayerFragment.createArgs(Gson().toJson(track))
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
        fun newInstance() = FavoritesTracksFragment()
    }
}