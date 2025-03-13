package com.example.playlistmaker

import android.content.Intent
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create

class SearchActivity : AppCompatActivity() {
    companion object {
        const val INPUT_SEARCH_TEXT = "INPUT_SEARCH_TEXT"
        const val INPUT_SEARCH_TEXT_DEF = ""
        const val BASE_URL_SEARCH = "https://itunes.apple.com/"
    }

    private var inputText = INPUT_SEARCH_TEXT_DEF
    private lateinit var editText: EditText
    private lateinit var buttonClear: ImageView
    private lateinit var buttonRefresh: MaterialButton
    private lateinit var toolbar: Toolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var notFoundPlaceholder:  LinearLayout
    private lateinit var failurePlaceholder: LinearLayout

    private val apiService = RetrofitFactory.create(BASE_URL_SEARCH).create<ItunesAPI>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        setupWindowInsets()

        initScreenView()
        setListeners()
        processInstanceState(savedInstanceState)
        setFocusScreen()
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

    private fun initScreenView() {
        editText = findViewById(R.id.search_edit_text)
        buttonClear = findViewById(R.id.search_clear_icon)
        buttonRefresh = findViewById(R.id.button_refresh)
        toolbar = findViewById(R.id.search_back)
        recyclerView = findViewById(R.id.search_recycler_view)
        notFoundPlaceholder = findViewById(R.id.search_not_found_placeholder)
        failurePlaceholder = findViewById(R.id.search_failure_placeholder)
    }

    private fun setListeners() {
        editText.addTextChangedListener(TextWatcher())

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchSongs(editText.text.toString())
            }
            false
        }
        
        buttonClear.setOnClickListener {
            createRecyclerView(arrayListOf())
            switchVisibilityView(SearchStatus.GOOD)
            editText.setText("")
            editText.clearFocus()
        }

        buttonRefresh.setOnClickListener {
            searchSongs(editText.text.toString())
        }

        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(INPUT_SEARCH_TEXT, inputText)
            startActivity(intent)
            finish()
        }
    }

    private fun searchSongs(text: String) {
        apiService.search(text)
            .enqueue(object : Callback<TrackResponse> {
                override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                    if (response.isSuccessful) {
                        val songs = response.body()?.results ?: arrayListOf()
                        if (songs.isNotEmpty()) {
                            switchVisibilityView(SearchStatus.GOOD)
                            createRecyclerView(songs)
                        } else {
                           switchVisibilityView(SearchStatus.NOT_FOUND)
                        }

                    } else {
                        switchVisibilityView(SearchStatus.FAILURE)
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    switchVisibilityView(SearchStatus.FAILURE)
                }
            })
    }

    private fun switchVisibilityView(status: SearchStatus) {
        when (status) {
            SearchStatus.GOOD -> {
                recyclerView.visibility = View.VISIBLE
                notFoundPlaceholder.visibility = View.GONE
                failurePlaceholder.visibility = View.GONE
            }
            SearchStatus.NOT_FOUND -> {
                recyclerView.visibility = View.GONE
                notFoundPlaceholder.visibility = View.VISIBLE
                failurePlaceholder.visibility = View.GONE
            }
            else -> {
                recyclerView.visibility = View.GONE
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

    private fun createRecyclerView(tracksList: ArrayList<Track>) {
        val trackAdapter = TrackAdapter(tracksList)
        recyclerView.adapter = trackAdapter
    }

    private fun TextWatcher(): TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // empty
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            buttonClear.isVisible = !s.isNullOrEmpty()
        }

        override fun afterTextChanged(s: Editable?) {
            val currentText = s.toString()
            if (currentText.isEmpty()) {
                createRecyclerView(arrayListOf())
            }
            inputText = currentText
        }
    }
}