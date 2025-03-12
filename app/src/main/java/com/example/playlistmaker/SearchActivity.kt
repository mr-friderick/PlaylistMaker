package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
    private lateinit var toolbar: Toolbar
    private lateinit var recyclerView: RecyclerView

    private val apiService = Retrofit.Builder()
        .baseUrl(BASE_URL_SEARCH)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create<ItunesAPI>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        setupWindowInsets()

        initScreenView()
        setListeners()
        processInstanceState(savedInstanceState)
        setFocusScreen()
        //createRecyclerView()

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
        toolbar = findViewById(R.id.search_back)
        recyclerView = findViewById(R.id.search_recycler_view)
    }

    private fun setListeners() {
        editText.addTextChangedListener(TextWatcher())

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchSongs(editText.text.toString())
                true
            }
            false
        }
        
        buttonClear.setOnClickListener {
            editText.setText("")
            editText.clearFocus()
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
                        val songs = response.body()!!.results
                        createRecyclerView(songs)
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    Log.e("SONG_SEARCH", "${t.message}")
                }

            })
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

    private fun tracksList(): ArrayList<Track> {
        return arrayListOf(
            Track(
                trackName = "Smells Like Teen Spirit",
                artistName = "Nirvana",
                trackTimeMillis = "5:01",
                artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                trackName = "Billie Jean",
                artistName = "Michael Jackson",
                trackTimeMillis = "4:35",
                artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
            ),
            Track(
                trackName = "Stayin' Alive",
                artistName = "Bee Gees",
                trackTimeMillis = "4:10",
                artworkUrl100 = "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                trackName = "Whole Lotta Love",
                artistName = "Led Zeppelin",
                trackTimeMillis = "5:33",
                artworkUrl100 = "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
            ),
            Track(
                trackName = "Sweet Child O'Mine",
                artistName = "Guns N' Roses",
                trackTimeMillis = "5:03",
                artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg "
            )
        )
    }

    private fun TextWatcher(): TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // empty
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            buttonClear.isVisible = !s.isNullOrEmpty()
        }

        override fun afterTextChanged(s: Editable?) {
            inputText = s.toString()
        }
    }
}