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
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewplaylistBinding
import com.example.playlistmaker.newplaylist.ui.viewmodel.NewPlaylistViewModel
import com.example.playlistmaker.newplaylist.ui.viewmodel.NewPlaylistViewState
import com.example.playlistmaker.playlist.ui.fragments.PlaylistFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class NewPlaylistFragment : Fragment() {

    private val viewModel by viewModel<NewPlaylistViewModel> {
        parametersOf(
            runCatching {
                requireArguments().getString(PlaylistFragment.Companion.ARGS_PLAYLIST)
            }.getOrDefault("")
        )
    }
    private var _binding: FragmentNewplaylistBinding? = null
    private val binding get() = _binding!!
    private lateinit var confirmDialog: MaterialAlertDialogBuilder
    private var uriCover: Uri? = null
    private val pickMedia = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            if (uriCover != uri) {
                uriChange = true
            }
            uriCover = uri
            Glide.with(this)
                .load(uriCover)
                .centerCrop()
                .into(binding.pictureCover)
        } else {
            // Пользователь отменил выбор
        }
    }
    private var uriChange = false
    private var isEditing = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewplaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initVariables()
        observeLiveData()
        setListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initVariables() {
        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.newplaylist_exit_question))
            .setMessage(getString(R.string.newplaylist_exit_message))
            .setNegativeButton(getString(R.string.newplaylist_exit_negative_button)) { dialog, which ->
                // Ничего не делаем
            }
            .setPositiveButton(getString(R.string.newplaylist_exit_positive_button)) { dialog, which ->
                findNavController().navigateUp()
            }
    }

    private fun observeLiveData() {
        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is NewPlaylistViewState.Default -> {
                    // Создание плейлиста
                }
                is NewPlaylistViewState.EditingPlaylist -> {
                    binding.apply {
                        isEditing = true

                        titleEditText.setText(state.model.title)
                        descriptionEditText.setText(state.model.description)

                        val file = File(context?.filesDir, state.model.picturePath)
                        Glide.with(pictureCover)
                            .load(file)
                            .placeholder(R.drawable.ic_newplaylist_choose)
                            .error(R.drawable.ic_newplaylist_choose)
                            .centerCrop()
                            .into(pictureCover)
                        uriCover = file.toUri()

                        toolbarBack.title = getString(R.string.newplaylist_edit_title)
                        create.text = getString(R.string.newplaylist_edit_button_text)
                    }
                }
            }
        }
    }

    private fun setListeners() {
        binding.toolbarBack.setOnClickListener {
            if (isEditing) {
                findNavController().navigateUp()
            } else {
                showExitDialog()
            }
        }

        binding.pictureCover.setOnClickListener {
            pickMedia.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        binding.create.setOnClickListener {
            if (isEditing) {
                savePlaylist()
            } else {
                createPlaylist()
            }
        }

        binding.titleEditText.doOnTextChanged { text, _, _, _ ->
            val notEmpty = !text.isNullOrBlank()
            binding.create.isEnabled = notEmpty
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (isEditing) {
                findNavController().navigateUp()
            } else {
                showExitDialog()
            }
        }
    }

    private fun savePlaylist() {
        val title = binding.titleEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.savePlaylist(
                title,
                description,
                uriCover,
                uriChange
            )

            findNavController().navigateUp()
        }
    }

    private fun createPlaylist() {
        val title = binding.titleEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()

        viewLifecycleOwner.lifecycleScope.launch {
            val result = viewModel.createPlaylist(
                title,
                description,
                uriCover
            )

            if (result.isSuccess) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.playlist_create_success, title),
                    Toast.LENGTH_SHORT
                ).show()

                findNavController().navigateUp()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.playlist_create_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
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

    companion object {
        const val ARGS_PLAYLIST = "playlist"

        fun createArgs(jsonPlaylist: String): Bundle {
            return bundleOf(ARGS_PLAYLIST to jsonPlaylist)
        }
    }
}