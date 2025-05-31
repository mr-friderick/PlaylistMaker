package com.example.playlistmaker.search.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.main.ui.activity.MainActivity
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.example.playlistmaker.search.ui.view_model.TrackAdapter
import com.example.playlistmaker.search.ui.view_model.SearchViewState
import com.google.gson.Gson

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: SearchViewModel

    private var isClickAllowed = true

    private var inputText = INPUT_SEARCH_TEXT_DEF

    private lateinit var historyAdapter: TrackAdapter
    private lateinit var mainHandler: Handler
    private lateinit var searchRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        initVariables()

        setContentView(binding.root)
        setupWindowInsets()

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

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.searchEditText.setText(savedInstanceState.getString(INPUT_SEARCH_TEXT, INPUT_SEARCH_TEXT_DEF))
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.screenSearch) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initVariables() {
        binding = ActivitySearchBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(
            this,
            SearchViewModel.getViewModelFactory()
        )[SearchViewModel::class.java]

        mainHandler = Handler(Looper.getMainLooper())
        searchRunnable = Runnable { viewModel.searchTracks(binding.searchEditText.text.toString()) }
    }

    private fun observeLiveData() {
        viewModel.historyLiveData.observe(this) { tracks ->
            historyAdapter = TrackAdapter(tracks) { track ->
                startPlayerActivity(track)
            }
            binding.searchHistoryRecyclerView.adapter = historyAdapter
        }

        viewModel.tracksLiveData.observe(this) { tracks ->
            createRecyclerView(tracks)
        }

        viewModel.stateLiveData.observe(this) { state ->
            binding.searchHistory.isVisible = state is SearchViewState.History
            binding.searchProgressBar.isVisible = state is SearchViewState.Loading
            binding.searchRecyclerView.isVisible = state is SearchViewState.Content
            binding.searchNotFoundPlaceholder.isVisible = state is SearchViewState.NotFound
            binding.searchFailurePlaceholder.isVisible = state is SearchViewState.Error
        }
    }

    private fun setListeners() {
        binding.searchEditText.addTextChangedListener(TextWatcher())

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchDebounce(false)
            }
            false
        }

        binding.searchEditText.setOnFocusChangeListener { _, _ ->
            if (historyAllowed()) {
                viewModel.setHistoryState()
            } else {
                viewModel.setDefaultState()
            }
        }

        binding.searchClearIcon.setOnClickListener {
            createRecyclerView(arrayListOf())
            binding.searchEditText.setText("")
            binding.searchEditText.clearFocus()

            viewModel.setDefaultState()
        }

        binding.buttonRefresh.setOnClickListener {
            viewModel.searchTracks(binding.searchEditText.text.toString())
        }

        binding.buttonClearHistory.setOnClickListener {
            viewModel.clearHistory()
        }

        binding.searchBack.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(INPUT_SEARCH_TEXT, inputText)
            startActivity(intent)
            finish()
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            mainHandler.postDelayed(
                { isClickAllowed = true },
                CLICK_DEBOUNCE_DELAY
            )
        }
        return current
    }

    private fun startPlayerActivity(track: Track) {
        if (clickDebounce()) {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra(INTENT_EXTRA_TRACK, Gson().toJson(track))
            startActivity(intent)
        }
    }

    private fun searchDebounce(needDelay: Boolean = true) {
        mainHandler.removeCallbacks(searchRunnable)
        if (needDelay) {
            mainHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        } else {
            mainHandler.post(searchRunnable)
        }
    }

    private fun processInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            inputText = savedInstanceState.getString(INPUT_SEARCH_TEXT, INPUT_SEARCH_TEXT_DEF)
            binding.searchEditText.setText(inputText)
        }
    }

    private fun setFocusScreen() {
        binding.searchEditText.requestFocus()
    }

    private fun defineCurrentView() {
        if (historyAllowed()) {
            viewModel.setHistoryState()
        } else {
            viewModel.setContentState()
        }
    }

    private fun createRecyclerView(tracksList: ArrayList<Track>) {
        val trackAdapter = TrackAdapter(tracksList) { track ->
            viewModel.addTrackInHistory(track)
            startPlayerActivity(track)
        }
        binding.searchRecyclerView.adapter = trackAdapter
    }

    private fun TextWatcher(): TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            binding.searchClearIcon.isVisible = !s.isNullOrEmpty()

            val textEmpty = s?.isEmpty() == true

            if (historyAllowed()) {
                viewModel.setHistoryState()
            } else if (!textEmpty) {
                searchDebounce()
            } else {
                viewModel.setDefaultState()
            }
        }

        override fun afterTextChanged(s: Editable?) {
            inputText = s.toString()
        }
    }

    private fun historyAllowed(): Boolean {
        return binding.searchEditText.hasFocus()
                && binding.searchEditText.text.isEmpty()
                && !viewModel.historyIsEmpty()
    }

    companion object {
        const val INPUT_SEARCH_TEXT = "INPUT_SEARCH_TEXT"
        const val INPUT_SEARCH_TEXT_DEF = ""
        const val INTENT_EXTRA_TRACK = "track"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}