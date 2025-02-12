package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible

class SearchActivity : AppCompatActivity() {
    companion object {
        const val INPUT_SEARCH_TEXT = "INPUT_SEARCH_TEXT"
        const val INPUT_SEARCH_TEXT_DEF = ""
    }

    private var inputText = INPUT_SEARCH_TEXT_DEF
    private lateinit var editText: EditText
    private lateinit var buttonClear: ImageView
    private lateinit var toolbar: Toolbar

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
        toolbar = findViewById(R.id.search_back)
    }

    private fun setListeners() {
        editText.addTextChangedListener(TextWatcher())

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

    private fun processInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            inputText = savedInstanceState.getString(INPUT_SEARCH_TEXT, INPUT_SEARCH_TEXT_DEF)
            editText.setText(inputText)
        }
    }

    private fun setFocusScreen() {
        editText.requestFocus()
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