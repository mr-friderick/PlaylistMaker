package com.example.playlistmaker.search.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.util.Creator
import com.example.playlistmaker.main.ui.activity.MainActivity
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.search.domain.interactors.HistoryInteractor
import com.example.playlistmaker.search.domain.interactors.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.example.playlistmaker.search.ui.view_model.TrackAdapter
import com.example.playlistmaker.settings.ui.view_model.SearchViewState
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: SearchViewModel

    private var isClickAllowed = true
    private var stopSearch = false
//    private lateinit var allDynamicView: List<View>
//    private lateinit var viewsByState: Map<CurrentView, List<View>>

    private var inputText = INPUT_SEARCH_TEXT_DEF

//    private lateinit var editText: EditText
//    private lateinit var buttonClear: ImageView
//    private lateinit var buttonRefresh: MaterialButton
//    private lateinit var buttonClearHistory: MaterialButton
//    private lateinit var toolbar: Toolbar
//    private lateinit var tracksRecyclerView: RecyclerView
//    private lateinit var historyRecyclerView: RecyclerView
//    private lateinit var historyView: LinearLayout
//    private lateinit var notFoundPlaceholder: LinearLayout
//    private lateinit var failurePlaceholder: LinearLayout
//    private lateinit var searchProgressBar: ProgressBar

//    private lateinit var tracksInteractor: TracksInteractor
//    private lateinit var historyInteractor: HistoryInteractor
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
            SearchViewModel.getViewModelFactory(applicationContext)
        )[SearchViewModel::class.java]

//        editText = findViewById(R.id.search_edit_text)
//        buttonClear = findViewById(R.id.search_clear_icon)
//        buttonRefresh = findViewById(R.id.button_refresh)
//        buttonClearHistory = findViewById(R.id.button_clear_history)
//        toolbar = findViewById(R.id.search_back)
//        historyView = findViewById(R.id.search_history)
//        tracksRecyclerView = findViewById(R.id.search_recycler_view)
//        historyRecyclerView = findViewById(R.id.search_history_recycler_view)
//        notFoundPlaceholder = findViewById(R.id.search_not_found_placeholder)
//        failurePlaceholder = findViewById(R.id.search_failure_placeholder)
//        searchProgressBar = findViewById(R.id.search_progress_bar)

//        allDynamicView = listOf(
//            binding.searchHistory,
//            binding.searchProgressBar,
//            binding.searchRecyclerView,
//            binding.searchNotFoundPlaceholder,
//            binding.searchFailurePlaceholder,
//        )
//
//        viewsByState = mapOf(
//            CurrentView.DEFAULT to listOf(),
//            CurrentView.HISTORY to listOf(binding.searchHistory),
//            CurrentView.SEARCH to listOf(binding.searchProgressBar),
//            CurrentView.TRACKS to listOf(binding.searchRecyclerView),
//            CurrentView.NOT_FOUND to listOf(binding.searchNotFoundPlaceholder),
//            CurrentView.NOT_CONNECTION to listOf(binding.searchFailurePlaceholder)
//        )

//        tracksInteractor = Creator.provideTracksInteractor()
//        historyInteractor = Creator.provideHistoryInteractor(this)

        mainHandler = Handler(Looper.getMainLooper())
        searchRunnable = Runnable { searchSongs(binding.searchEditText.text.toString()) }
    }

    private fun observeLiveData() {
        viewModel.historyLiveData.observe(this) { tracks ->
            historyAdapter = TrackAdapter(tracks) { track ->
                startPlayerActivity(track)
            }
            binding.searchHistoryRecyclerView.adapter = historyAdapter
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
                //switchVisibilityView(CurrentView.HISTORY)
            } else {
                //switchVisibilityView(CurrentView.TRACKS)
            }
        }

        binding.searchClearIcon.setOnClickListener {
            createRecyclerView(binding.searchRecyclerView, arrayListOf())
            //switchVisibilityView(CurrentView.DEFAULT)
            binding.searchEditText.setText("")
            binding.searchEditText.clearFocus()
        }

        binding.buttonRefresh.setOnClickListener {
            searchSongs(binding.searchEditText.text.toString())
        }

        binding.buttonClearHistory.setOnClickListener {
            viewModel.clearHistory()
            //historyAdapter.updateData(historyInteractor.read())
            //switchVisibilityView(CurrentView.TRACKS)
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

    private fun searchSongs(text: String) {
        if (text.isEmpty()) return

        //switchVisibilityView(CurrentView.SEARCH)

        tracksInteractor.searchTracks(
            text,
            object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: ArrayList<Track>, isError: Boolean) {
                    runOnUiThread {
                        if (stopSearch) {
                            if (historyAllowed()) {
                                //switchVisibilityView(CurrentView.HISTORY)
                            } else {
                                //switchVisibilityView(CurrentView.TRACKS)
                            }
                            return@runOnUiThread
                        }

                        if (foundTracks.isEmpty()) {
                            if (isError) {
                                //switchVisibilityView(CurrentView.NOT_CONNECTION)
                            } else {
                                //switchVisibilityView(CurrentView.NOT_FOUND)
                            }
                        } else {
                            //switchVisibilityView(CurrentView.TRACKS)
                            createRecyclerView(binding.searchRecyclerView, foundTracks)
                        }
                    }
                }
            }
        )
    }

//    private fun switchVisibilityView(status: CurrentView) {
//        allDynamicView.forEach { it.isVisible = false }
//        viewsByState[status]?.forEach { it.isVisible = true }
//    }

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
            //switchVisibilityView(CurrentView.HISTORY)
        } else {
            //switchVisibilityView(CurrentView.TRACKS)
        }
    }

    private fun createRecyclerView(recyclerView: RecyclerView, tracksList: ArrayList<Track>) {
        val trackAdapter = TrackAdapter(tracksList) { track ->
            viewModel.addTrackInHistory(track)
            startPlayerActivity(track)
        }
        recyclerView.adapter = trackAdapter
    }

    private fun TextWatcher(): TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            binding.searchClearIcon.isVisible = !s.isNullOrEmpty()

            val textEmpty = s?.isEmpty() == true
            stopSearch = textEmpty

            if (historyAllowed()) {
                //switchVisibilityView(CurrentView.HISTORY)
            } else if (!textEmpty) {
                searchDebounce()
            } else //switchVisibilityView(CurrentView.DEFAULT)
        }

        override fun afterTextChanged(s: Editable?) {
            inputText = s.toString()
        }
    }

    private fun historyAllowed() = binding.searchEditText.hasFocus() && binding.searchEditText.text.isEmpty() && !viewModel.historyIsEmpty()

    companion object {
        const val INPUT_SEARCH_TEXT = "INPUT_SEARCH_TEXT"
        const val INPUT_SEARCH_TEXT_DEF = ""
        const val INTENT_EXTRA_TRACK = "track"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L

        enum class CurrentView {
            DEFAULT, HISTORY, SEARCH, TRACKS, NOT_FOUND, NOT_CONNECTION
        }
    }
}