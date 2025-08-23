package com.example.playlistmaker.medialibrary.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.medialibrary.ui.viewmodel.PlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<PlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
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
        //TODO("Not yet implemented")
    }

    private fun setListeners() {
        binding.buttonNewPlaylist.setOnClickListener {
            findNavController().navigate(
                R.id.action_mediaRootFragment_to_newPlaylistFragment
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance() = PlaylistFragment().apply {
            arguments = Bundle().apply {
                // TODO (Реализация будет в будущем)
            }
        }
    }
}