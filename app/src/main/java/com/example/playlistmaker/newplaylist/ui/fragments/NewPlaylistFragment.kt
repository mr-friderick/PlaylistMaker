package com.example.playlistmaker.newplaylist.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.databinding.FragmentNewplaylistBinding
import com.example.playlistmaker.newplaylist.ui.viewmodel.NewPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPlaylistFragment: Fragment() {

    private val viewModel by viewModel<NewPlaylistViewModel>()
    private lateinit var binding: FragmentNewplaylistBinding
    private val pickMedia = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            uriCover = uri
            Glide.with(this)
                .load(uriCover)
                .centerCrop()
                .into(binding.pictureCover)
        } else {
            // Пользователь отменил выбор
        }
    }
    private var uriCover: Uri? = null
    private lateinit var confirmDialog: MaterialAlertDialogBuilder

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
        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNegativeButton("Отмена") { dialog, which ->
                // Ничего не делаем
            }.setPositiveButton("Завершить") { dialog, which ->
                findNavController().navigateUp()
            }
    }

    private fun observeLiveData() {
        //TODO("Not yet implemented")
    }

    private fun setListeners() {
        binding.toolbarBack.setOnClickListener {
            showExitDialog()
        }

        binding.pictureCover.setOnClickListener {
            pickMedia.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        binding.create.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val description = binding.descriptionEditText.text.toString()

            viewModel.createPlaylist(
                title,
                description,
                uriCover
            )

            Toast.makeText(requireContext(),
                "Плейлист $title создан",
                Toast.LENGTH_SHORT
            ).show()

            findNavController().navigateUp()
        }

        binding.titleEditText.doOnTextChanged { text, _, _, _ ->
            binding.create.isEnabled = !text.isNullOrBlank()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showExitDialog()
        }
    }

    private fun showExitDialog() {
        if (isModified()) {
            confirmDialog.show()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun isModified(): Boolean {
        return uriCover != null
                || binding.titleEditText.text?.isBlank() == false
                || binding.descriptionEditText.text?.isBlank() == false
    }
}