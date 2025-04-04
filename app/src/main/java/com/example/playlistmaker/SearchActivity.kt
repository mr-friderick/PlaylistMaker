package com.example.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create

class SearchActivity : AppCompatActivity() {
    companion object {
        const val INPUT_SEARCH_TEXT = "INPUT_SEARCH_TEXT"
        const val INPUT_SEARCH_TEXT_DEF = ""
        const val BASE_URL_SEARCH = "https://itunes.apple.com/"
        const val INTENT_EXTRA_TRACK = "track"

        enum class CurrentView {
            HISTORY, TRACKS, NOT_FOUND, NOT_CONNECTION
        }
    }

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
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var searchHistory: SearchHistory
    private lateinit var historyAdapter: TrackAdapter

    private val apiService = RetrofitFactory.create(BASE_URL_SEARCH).create<ItunesAPI>()

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

        sharedPrefs = getSharedPreferences(SearchHistory.FILE_HISTORY_PREFERENCES, MODE_PRIVATE)

        searchHistory = SearchHistory(sharedPrefs)
        historyAdapter = TrackAdapter(searchHistory.tracksList()) { track ->
            startPlayerActivity(track)
        }
        historyRecyclerView.adapter = historyAdapter
    }

    private fun setListeners() {
        editText.addTextChangedListener(TextWatcher())

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchSongs(editText.text.toString())
            }
            false
        }

        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !searchHistory.empty()) {
                switchVisibilityView(CurrentView.HISTORY)
            } else switchVisibilityView(CurrentView.TRACKS)
        }

        buttonClear.setOnClickListener {
            createRecyclerView(tracksRecyclerView, arrayListOf())
            switchVisibilityView(CurrentView.TRACKS)
            editText.setText("")
            editText.clearFocus()
        }

        buttonRefresh.setOnClickListener {
            searchSongs(editText.text.toString())
        }

        buttonClearHistory.setOnClickListener {
            searchHistory.clear()
            historyAdapter.updateData(searchHistory.tracksList())
            switchVisibilityView(CurrentView.TRACKS)
        }

        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(INPUT_SEARCH_TEXT, inputText)
            startActivity(intent)
            finish()
        }
    }

    private fun startPlayerActivity(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra(INTENT_EXTRA_TRACK, Gson().toJson(track))
        startActivity(intent)
    }

    private fun searchSongs(text: String) {
        apiService.search(text)
            .enqueue(object : Callback<TrackResponse> {
                override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                    if (response.isSuccessful) {
                        val tracks = response.body()?.results ?: arrayListOf()
                        if (tracks.isNotEmpty()) {
                            switchVisibilityView(CurrentView.TRACKS)
                            createRecyclerView(tracksRecyclerView, tracks)
                        } else {
                           switchVisibilityView(CurrentView.NOT_FOUND)
                        }

                    } else {
                        switchVisibilityView(CurrentView.NOT_CONNECTION)
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    switchVisibilityView(CurrentView.NOT_CONNECTION)
                }
            })
    }

    private fun switchVisibilityView(status: CurrentView) {
        when (status) {
            CurrentView.HISTORY -> {
                historyView.visibility = View.VISIBLE
                tracksRecyclerView.visibility = View.GONE
                notFoundPlaceholder.visibility = View.GONE
                failurePlaceholder.visibility = View.GONE
            }
            CurrentView.TRACKS -> {
                historyView.visibility = View.GONE
                tracksRecyclerView.visibility = View.VISIBLE
                notFoundPlaceholder.visibility = View.GONE
                failurePlaceholder.visibility = View.GONE
            }
            CurrentView.NOT_FOUND -> {
                historyView.visibility = View.GONE
                tracksRecyclerView.visibility = View.GONE
                notFoundPlaceholder.visibility = View.VISIBLE
                failurePlaceholder.visibility = View.GONE
            }
            // CurrentView.NOT_CONNECTION
            else -> {
                historyView.visibility = View.GONE
                tracksRecyclerView.visibility = View.GONE
                notFoundPlaceholder.visibility = View.GONE
                failurePlaceholder.visibility = View.VISIBLE
            }
        }
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
        if (!searchHistory.empty()) {
            switchVisibilityView(CurrentView.HISTORY)
        } else switchVisibilityView(CurrentView.TRACKS)
    }

    private fun createRecyclerView(recyclerView: RecyclerView, tracksList: ArrayList<Track>) {
        val trackAdapter = TrackAdapter(tracksList) { track ->
            searchHistory.add(track)
            historyAdapter.updateData(searchHistory.tracksList())

            startPlayerActivity(track)
        }
        recyclerView.adapter = trackAdapter
    }

    private fun TextWatcher(): TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            buttonClear.isVisible = !s.isNullOrEmpty()
            if (editText.hasFocus() && s?.isEmpty() == true) {
                switchVisibilityView(CurrentView.HISTORY)
            } else switchVisibilityView(CurrentView.TRACKS)
        }

        override fun afterTextChanged(s: Editable?) {
            val currentText = s.toString()
            if (currentText.isEmpty()) {
                createRecyclerView(tracksRecyclerView, arrayListOf())
                switchVisibilityView(CurrentView.TRACKS)
            }
            inputText = currentText
        }
    }
}