package com.example.towerbit

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.widget.ImageView

class MainMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_menu)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN       // Полноэкранный режим (скрытие статусной строки)
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Скрытие навигационной панели
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) // Восстановление системных панелей при свайпе


        // Обрабатываем WindowInsets для корректной работы с системными панелями
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)

            // Возвращаем insets, чтобы ошибка исчезла
            insets
        }

        // Анимация изображения
        val animatedImage = findViewById<ImageView>(R.id.main_image)
        val animationDrawable = AnimationDrawable()

        // Добавляем кадры
        animationDrawable.addFrame(getDrawable(R.drawable.fs0)!!, 100)
        animationDrawable.addFrame(getDrawable(R.drawable.fs1)!!, 100)
        animationDrawable.addFrame(getDrawable(R.drawable.fs2)!!, 100)
        animationDrawable.addFrame(getDrawable(R.drawable.fs3)!!, 100)

        // Устанавливаем анимацию для ImageView
        animatedImage.setImageDrawable(animationDrawable)

        // Запускаем анимацию
        animationDrawable.isOneShot = false
        animatedImage.post { animationDrawable.start() }
    }
}
