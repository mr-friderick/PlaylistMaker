package com.example.playlistmaker.newplaylist.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.databinding.FragmentNewplaylistBinding
import com.example.playlistmaker.newplaylist.ui.viewmodel.NewPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPlaylistFragment: Fragment() {

    private val viewModel by viewModel<NewPlaylistViewModel>()
    private lateinit var binding: FragmentNewplaylistBinding
    private val pickMedia = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            binding.pictureCover.setImageURI(uri)
        } else {
            // Пользователь отменил выбор
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewplaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initVariables()
        observeLiveData()
        setListeners()
    }

    private fun initVariables() {

    }

    private fun observeLiveData() {
        //TODO("Not yet implemented")
    }

    private fun setListeners() {
        binding.toolbarBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.create.setOnClickListener {
            pickMedia.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }
}