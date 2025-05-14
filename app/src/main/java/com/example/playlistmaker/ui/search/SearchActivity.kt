package com.example.playlistmaker.ui.search

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
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.ui.main.MainActivity
import com.example.playlistmaker.ui.player.PlayerActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.interactors.HistoryInteractor
import com.example.playlistmaker.domain.interactors.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.search.TrackAdapter
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson

class SearchActivity : AppCompatActivity() {
    private var isClickAllowed = true
    private var stopSearch = false
    private lateinit var allDynamicView: List<View>
    private lateinit var viewsByState: Map<CurrentView, List<View>>

    private var inputText = INPUT_SEARCH_TEXT_DEF
    private lateinit var editText: EditText
    private lateinit var buttonClear: ImageView
    private lateinit var buttonRefresh: MaterialButton
    private lateinit var buttonClearHistory: MaterialButton
    private lateinit var toolbar: Toolbar
    private lateinit var tracksRecyclerView: RecyclerView
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyView: LinearLayout
    private lateinit var notFoundPlaceholder: LinearLayout
    private lateinit var failurePlaceholder: LinearLayout
    private lateinit var searchProgressBar: ProgressBar

    private lateinit var tracksInteractor: TracksInteractor
    private lateinit var historyInteractor: HistoryInteractor
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var mainHandler: Handler
    private lateinit var searchRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        setupWindowInsets()

        initVariables()
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
        editText.setText(savedInstanceState.getString(INPUT_SEARCH_TEXT, INPUT_SEARCH_TEXT_DEF))
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.screen_search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initVariables() {
        editText = findViewById(R.id.search_edit_text)
        buttonClear = findViewById(R.id.search_clear_icon)
        buttonRefresh = findViewById(R.id.button_refresh)
        buttonClearHistory = findViewById(R.id.button_clear_history)
        toolbar = findViewById(R.id.search_back)
        historyView = findViewById(R.id.search_history)
        tracksRecyclerView = findViewById(R.id.search_recycler_view)
        historyRecyclerView = findViewById(R.id.search_history_recycler_view)
        notFoundPlaceholder = findViewById(R.id.search_not_found_placeholder)
        failurePlaceholder = findViewById(R.id.search_failure_placeholder)
        searchProgressBar = findViewById(R.id.search_progress_bar)

        allDynamicView = listOf(
            historyView,
            searchProgressBar,
            tracksRecyclerView,
            notFoundPlaceholder,
            failurePlaceholder,
        )

        viewsByState = mapOf(
            CurrentView.DEFAULT to listOf(),
            CurrentView.HISTORY to listOf(historyView),
            CurrentView.SEARCH to listOf(searchProgressBar),
            CurrentView.TRACKS to listOf(tracksRecyclerView),
            CurrentView.NOT_FOUND to listOf(notFoundPlaceholder),
            CurrentView.NOT_CONNECTION to listOf(failurePlaceholder)
        )

        tracksInteractor = Creator.provideTracksInteractor()
        historyInteractor = Creator.provideHistoryInteractor(this)

        historyAdapter = TrackAdapter(historyInteractor.read()) { track ->
            startPlayerActivity(track)
        }
        historyRecyclerView.adapter = historyAdapter

        mainHandler = Handler(Looper.getMainLooper())
        searchRunnable = Runnable { searchSongs(editText.text.toString()) }
    }

    private fun setListeners() {
        editText.addTextChangedListener(TextWatcher())

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchDebounce(false)
            }
            false
        }

        editText.setOnFocusChangeListener { _, _ ->
            if (historyAllowed()) {
                switchVisibilityView(CurrentView.HISTORY)
            } else switchVisibilityView(CurrentView.TRACKS)
        }

        buttonClear.setOnClickListener {
            createRecyclerView(tracksRecyclerView, arrayListOf())
            switchVisibilityView(CurrentView.DEFAULT)
            editText.setText("")
            editText.clearFocus()
        }

        buttonRefresh.setOnClickListener {
            searchSongs(editText.text.toString())
        }

        buttonClearHistory.setOnClickListener {
            historyInteractor.clear()
            historyAdapter.updateData(historyInteractor.read())
            switchVisibilityView(CurrentView.TRACKS)
        }

        toolbar.setNavigationOnClickListener {
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

        switchVisibilityView(CurrentView.SEARCH)

        tracksInteractor.searchTracks(
            text,
            object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: ArrayList<Track>, isError: Boolean) {
                    runOnUiThread {
                        if (stopSearch) {
                            if (historyAllowed()) {
                                switchVisibilityView(CurrentView.HISTORY)
                            } else {
                                switchVisibilityView(CurrentView.TRACKS)
                            }
                            return@runOnUiThread
                        }

                        if (foundTracks.isEmpty()) {
                            if (isError) {
                                switchVisibilityView(CurrentView.NOT_CONNECTION)
                            } else {
                                switchVisibilityView(CurrentView.NOT_FOUND)
                            }
                        } else {
                            switchVisibilityView(CurrentView.TRACKS)
                            createRecyclerView(tracksRecyclerView, foundTracks)
                        }
                    }
                }
            }
        )
    }

    private fun switchVisibilityView(status: CurrentView) {
        allDynamicView.forEach { it.isVisible = false }
        viewsByState[status]?.forEach { it.isVisible = true }
    }

    private fun processInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            inputText = savedInstanceState.getString(INPUT_SEARCH_TEXT, INPUT_SEARCH_TEXT_DEF)
            editText.setText(inputText)
        }
    }

    private fun setFocusScreen() {
        editText.requestFocus()
    }

    private fun defineCurrentView() {
        if (historyAllowed()) {
            switchVisibilityView(CurrentView.HISTORY)
        } else {
            switchVisibilityView(CurrentView.TRACKS)
        }
    }

    private fun createRecyclerView(recyclerView: RecyclerView, tracksList: ArrayList<Track>) {
        val trackAdapter = TrackAdapter(tracksList) { track ->
            historyInteractor.add(track)
            historyAdapter.updateData(historyInteractor.read())

            startPlayerActivity(track)
        }
        recyclerView.adapter = trackAdapter
    }

    private fun TextWatcher(): TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            buttonClear.isVisible = !s.isNullOrEmpty()

            val textEmpty = s?.isEmpty() == true
            stopSearch = textEmpty

            if (historyAllowed()) {
                switchVisibilityView(CurrentView.HISTORY)
            } else if (!textEmpty) {
                searchDebounce()
            } else switchVisibilityView(CurrentView.DEFAULT)
        }

        override fun afterTextChanged(s: Editable?) {
            inputText = s.toString()
        }
    }

    private fun historyAllowed() = editText.hasFocus() && editText.text.isEmpty() && !historyInteractor.isEmpty()

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