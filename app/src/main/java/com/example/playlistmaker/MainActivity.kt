package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Обработка кнопки Поиск через анонимный класс
        val buttonSearch = findViewById<Button>(R.id.search)
        val buttonSearchClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Выполняю поиск!", Toast.LENGTH_SHORT).show()
            }
        }
        buttonSearch.setOnClickListener(buttonSearchClickListener)

        // Обработка кнопки Медиатека через лямбда-выражение
        val buttonMedia = findViewById<Button>(R.id.media)
        buttonMedia.setOnClickListener {
            Toast.makeText(this@MainActivity, "Открываю медиатеку!", Toast.LENGTH_SHORT).show()
        }

        // Обработка кнопки Настройки через лямбда-выражение
        findViewById<Button>(R.id.settings).setOnClickListener {
            Toast.makeText(this@MainActivity, "Открываю настройки!", Toast.LENGTH_SHORT).show()
        }
    }
}