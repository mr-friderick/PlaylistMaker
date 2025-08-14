package com.example.playlistmaker.search.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.fragments.PlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.search.ui.viewmodel.SearchViewState
import com.example.playlistmaker.search.ui.adapter.TrackAdapter
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val clickDebounceDelay = 1000L
    private lateinit var binding: FragmentSearchBinding
    private val viewModel by viewModel<SearchViewModel>()
    private var isClickAllowed = true
    private var inputText = INPUT_SEARCH_TEXT_DEF
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var iim: InputMethodManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initVariables()
        observeLiveData()
        setListeners()
        processInstanceState(savedInstanceState)
        setFocusScreen()
        defineCurrentView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_SEARCH_TEXT, inputText)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            binding.searchEditText.setText(
                savedInstanceState.getString(
                    INPUT_SEARCH_TEXT,
                    INPUT_SEARCH_TEXT_DEF
                )
            )
        }
    }

    private fun initVariables() {
        iim = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    private fun observeLiveData() {
        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            hideAllDynamicView()
            when (state) {
                SearchViewState.Default -> {}
                is SearchViewState.History -> {
                    historyAdapter = TrackAdapter(state.historyTracks) { track ->
                        startPlayerFragment(track)
                    }
                    binding.searchHistoryRecyclerView.adapter = historyAdapter
                    binding.searchHistory.isVisible = true
                }

                SearchViewState.Loading -> {
                    binding.searchProgressBar.isVisible = true
                }

                is SearchViewState.Content -> {
                    createRecyclerView(state.contentTracks)
                    binding.searchRecyclerView.isVisible = true
                }

                SearchViewState.NotFound -> {
                    binding.searchNotFoundPlaceholder.isVisible = true
                }

                SearchViewState.Error -> {
                    binding.searchFailurePlaceholder.isVisible = true
                }
            }
        }
    }

    private fun setListeners() {
        binding.searchEditText.addTextChangedListener(
            onTextChanged = { text, _, _, _ ->
                binding.searchClearIcon.isVisible = !text.isNullOrEmpty()

                val textEmpty = text?.isEmpty() == true

                if (historyAllowed()) {
                    viewModel.setHistoryState()
                } else if (!textEmpty) {
                    searchDebounce()
                } else {
                    viewModel.setDefaultState()
                }
            },
            afterTextChanged = { text ->
                inputText = text.toString()
            }
        )

        binding.searchEditText.apply {
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    searchDebounce(false)
                }
                false
            }

            setOnFocusChangeListener { _, _ ->
                if (historyAllowed()) {
                    viewModel.setHistoryState()
                    setFocusScreen()
                } else {
                    viewModel.setDefaultState()
                }
            }
        }

        binding.searchClearIcon.setOnClickListener {
            createRecyclerView(arrayListOf())
            binding.searchEditText.setText("")
            clearFocusScreen()

            viewModel.setDefaultState()
        }

        binding.buttonRefresh.setOnClickListener {
            viewModel.searchTracks(binding.searchEditText.text.toString())
        }

        binding.buttonClearHistory.setOnClickListener {
            viewModel.clearHistory()
        }
    }

    private fun processInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            inputText = savedInstanceState.getString(
                INPUT_SEARCH_TEXT,
                INPUT_SEARCH_TEXT_DEF
            )
            binding.searchEditText.setText(inputText)
        }
    }

    private fun setFocusScreen() {
        binding.searchEditText.requestFocus()
        iim.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun clearFocusScreen() {
        binding.searchEditText.clearFocus()
        iim.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    private fun defineCurrentView() {
        if (historyAllowed()) {
            viewModel.setHistoryState()
        } else {
            viewModel.setDefaultState()
        }
    }

    private fun hideAllDynamicView() {
        binding.searchHistory.isVisible = false
        binding.searchProgressBar.isVisible = false
        binding.searchRecyclerView.isVisible = false
        binding.searchNotFoundPlaceholder.isVisible = false
        binding.searchFailurePlaceholder.isVisible = false
    }

    private fun startPlayerFragment(track: Track) {
        if (clickDebounce()) {
            findNavController().navigate(
                R.id.action_searchFragment_to_playerFragment,
                PlayerFragment.createArgs(Gson().toJson(track))
            )
        }
    }

    private fun searchDebounce(needDelay: Boolean = true) {
        viewModel.searchTracks(
            binding.searchEditText.text.toString(), needDelay
        )
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

    private fun createRecyclerView(tracksList: ArrayList<Track>) {
        val trackAdapter = TrackAdapter(tracksList) { track ->
            viewModel.addTrackInHistory(track)
            startPlayerFragment(track)
        }
        binding.searchRecyclerView.adapter = trackAdapter
    }

    private fun historyAllowed(): Boolean {
        return binding.searchEditText.hasFocus()
                && binding.searchEditText.text.isEmpty()
                && !viewModel.historyIsEmpty()
    }

    companion object {
        const val INPUT_SEARCH_TEXT = "INPUT_SEARCH_TEXT"
        const val INPUT_SEARCH_TEXT_DEF = ""
    }
}