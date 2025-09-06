package com.example.playlistmaker.playlist.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.newplaylist.ui.fragments.NewPlaylistFragment
import com.example.playlistmaker.player.ui.fragments.PlayerFragment
import com.example.playlistmaker.playlist.ui.viewmodel.PlaylistViewModel
import com.example.playlistmaker.playlist.ui.viewmodel.PlaylistViewState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.adapter.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class PlaylistFragment : Fragment() {

    private val clickDebounceDelay = 1000L
    private var isClickAllowed = true
    private  val gson = Gson()
    private val viewModel by viewModel<PlaylistViewModel> {
        parametersOf(requireArguments().getInt(ARGS_PLAYLIST))
    }
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private lateinit var tracksAdapter: TrackAdapter
    private lateinit var confirmDialog: MaterialAlertDialogBuilder
    private lateinit var bottomSheetTracksBehavior: BottomSheetBehavior<View>
    private lateinit var bottomSheetMenuBehavior: BottomSheetBehavior<View>
    private var playlistTracksCountText: String = ""


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
        viewModel.setDefaultState()
        setBottomSheet(bottomSheetMenuBehavior, BottomSheetBehavior.STATE_HIDDEN)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initVariables() {
        bottomSheetTracksBehavior = BottomSheetBehavior.from(binding.playlistBottomSheetInclude.root)
        bottomSheetMenuBehavior = BottomSheetBehavior.from(binding.playlistBottomSheetMenuInclude.root)
    }

    private fun observeLiveData() {
        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            when(state) {
                is PlaylistViewState.Default -> {
                    binding.apply {
                        playlistName.text = state.model.title
                        playlistDescription.text = state.model.description

                        playlistTracksCountText = tracksCountText(state.model.tracksCount)
                        playlistTracksCount.text = playlistTracksCountText

                        playlistTime.text = resources.getQuantityString(
                            R.plurals.playlist_time,
                            state.playlistTime.toInt(),
                            state.playlistTime
                        )

                        setPlaylistCover(playlistCover, state.model.picturePath)

                        tracksAdapter = TrackAdapter(
                            tracks = state.tracks.toCollection(ArrayList()),
                            clickItem = { startPlayerFragment(it) },
                            longClickItem = {
                                buildConfirmDialog(
                                    message = getString(R.string.playlist_longclick_message),
                                    positiveCallback = { viewModel.deleteTrack(it) }
                                )
                                confirmDialog.show()
                            }
                        )
                        binding.playlistBottomSheetInclude.playlistRecyclerView.adapter = tracksAdapter
                    }
                }
            }
        }
    }

    private fun setListeners() {
        binding.apply {
            playlistButtonBack.setOnClickListener {
                findNavController().navigateUp()
            }

            playlistMenu.setOnClickListener {
                val dataForMenu = viewModel.dataForMenu()

                setBottomSheet(bottomSheetMenuBehavior, BottomSheetBehavior.STATE_COLLAPSED)
                binding.overlay.isVisible = true

                binding.playlistBottomSheetMenuInclude.apply {
                    playlistMenuName.text = dataForMenu["title"] as String
                    playlistMenuTracksCount.text = tracksCountText(dataForMenu["trackCount"] as Int)

                    setPlaylistCover(playlistMenuCover, dataForMenu["coverPath"] as String)
                }
            }

            playlistShare.setOnClickListener {
                startShareActivity()
            }

            playlistBottomSheetMenuInclude.apply {
                playlistMenuShare.setOnClickListener {
                    startShareActivity()
                }

                playlistMenuDelete.setOnClickListener {
                    buildConfirmDialog(
                        message = getString(
                            R.string.playlist_menu_delete_message,
                            viewModel.playlistName()
                        ),
                        positiveCallback = {
                            viewModel.deletePlaylist()
                            findNavController().navigateUp()
                        }
                    )
                    confirmDialog.show()
                }

                playlistMenuEdit.setOnClickListener {
                    startEditFragment(viewModel.modelToGson())
                }
            }
        }

        bottomSheetTracksBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.overlay.isVisible = false
                    }

                    else -> {
                        binding.overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        bottomSheetMenuBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                    }

                    else -> {
                        binding.overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    fun buildConfirmDialog(message: String, positiveCallback: () -> Unit, negativeCallback: () -> Unit = {}) {
        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setMessage(message)
            .setNegativeButton(getString(R.string.playlist_longlick_negative_button)) { dialog, which ->
                negativeCallback()
            }
            .setPositiveButton(getString(R.string.playlist_longlick_positive_button)) { dialog, which ->
                positiveCallback()
            }
    }

    private fun startShareActivity() {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, viewModel.messageForShare(playlistTracksCountText))
        }
        startActivity(Intent.createChooser(intent, ""))
    }

    private fun startPlayerFragment(track: Track) {
        if (clickDebounce()) {
            findNavController().navigate(
                R.id.action_playlistFragment_to_playerFragment,
                PlayerFragment.createArgs(gson.toJson(track))
            )
        }
    }

    private fun startEditFragment(playlist: String) {
        if (clickDebounce()) {
            findNavController().navigate(
                R.id.action_playlistFragment_to_newPlaylistFragment,
                NewPlaylistFragment.createArgs(playlist)
            )
        }
    }

    fun tracksCountText(count: Int): String {
        return resources.getQuantityString(
            R.plurals.tracks_count,
            count,
            count
        )
    }

    fun setPlaylistCover(view: ImageView, path: String) {
        val file = File(context?.filesDir, path)
        Glide.with(view)
            .load(file)
            .placeholder(R.drawable.ic_playlist_placeholder)
            .error(R.drawable.ic_playlist_placeholder)
            .centerCrop()
            .into(view)
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

    private fun setBottomSheet(bottomSheet: BottomSheetBehavior<View>, state: Int) {
        bottomSheet.state = state
    }

    companion object {
        const val ARGS_PLAYLIST = "playlist"

        fun createArgs(playlistId: Int): Bundle {
            return bundleOf(ARGS_PLAYLIST to playlistId)
        }
    }
}