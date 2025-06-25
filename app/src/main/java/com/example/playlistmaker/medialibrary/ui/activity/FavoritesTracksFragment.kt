package com.example.playlistmaker.medialibrary.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getString
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentFavoritesTracksBinding
import com.example.playlistmaker.medialibrary.ui.viewmodel.FavoritesTracksViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesTracksFragment : Fragment() {

    private var _binding: FragmentFavoritesTracksBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<FavoritesTracksViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance() = FavoritesTracksFragment()
    }
}