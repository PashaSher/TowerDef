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
        animationDrawable.addFrame(getDrawable(R.drawable.fs0)!!, 300)
        animationDrawable.addFrame(getDrawable(R.drawable.fs1)!!, 300)
        animationDrawable.addFrame(getDrawable(R.drawable.fs2)!!, 300)
        animationDrawable.addFrame(getDrawable(R.drawable.fs3)!!, 300)
        animationDrawable.addFrame(getDrawable(R.drawable.fs4)!!, 300)
        animationDrawable.addFrame(getDrawable(R.drawable.fs5)!!, 300)
        animationDrawable.addFrame(getDrawable(R.drawable.fs6)!!, 300)
        animationDrawable.addFrame(getDrawable(R.drawable.fs7)!!, 300)
        animationDrawable.addFrame(getDrawable(R.drawable.fs8)!!, 300)
        animationDrawable.addFrame(getDrawable(R.drawable.fs9)!!, 300)
        animationDrawable.addFrame(getDrawable(R.drawable.fs10)!!, 200)
        animationDrawable.addFrame(getDrawable(R.drawable.fs11)!!, 200)
        animationDrawable.addFrame(getDrawable(R.drawable.fs12)!!, 200)
        animationDrawable.addFrame(getDrawable(R.drawable.fs13)!!, 200)
        animationDrawable.addFrame(getDrawable(R.drawable.fs14)!!, 200)
        animationDrawable.addFrame(getDrawable(R.drawable.fs15)!!, 200)
        animationDrawable.addFrame(getDrawable(R.drawable.fs16)!!, 200)
        animationDrawable.addFrame(getDrawable(R.drawable.fs17)!!, 200)
        animationDrawable.addFrame(getDrawable(R.drawable.fs18)!!, 200)
        animationDrawable.addFrame(getDrawable(R.drawable.fs19)!!, 200)
        animationDrawable.addFrame(getDrawable(R.drawable.fs20)!!, 200)
        animationDrawable.addFrame(getDrawable(R.drawable.fs21)!!, 200)
        animationDrawable.addFrame(getDrawable(R.drawable.fs22)!!, 200)
        animationDrawable.addFrame(getDrawable(R.drawable.fs23)!!, 200)
        animationDrawable.addFrame(getDrawable(R.drawable.fs24)!!, 200)
        animationDrawable.addFrame(getDrawable(R.drawable.fs25)!!, 200)
        animationDrawable.addFrame(getDrawable(R.drawable.fs26)!!, 200)
        animationDrawable.addFrame(getDrawable(R.drawable.fs27)!!, 200)
        animationDrawable.addFrame(getDrawable(R.drawable.fs28)!!, 200)
        animationDrawable.addFrame(getDrawable(R.drawable.fs29)!!, 200)
        animationDrawable.addFrame(getDrawable(R.drawable.fs30)!!, 200)
        animationDrawable.addFrame(getDrawable(R.drawable.fs31)!!, 200)
        animationDrawable.addFrame(getDrawable(R.drawable.fs32)!!, 200)
        animationDrawable.addFrame(getDrawable(R.drawable.fs33)!!, 200)
        animationDrawable.addFrame(getDrawable(R.drawable.fs34)!!, 200)
        animationDrawable.addFrame(getDrawable(R.drawable.fs35)!!, 200)



        // Устанавливаем анимацию для ImageView
        animatedImage.setImageDrawable(animationDrawable)

        // Запускаем анимацию
        animationDrawable.isOneShot = true
        animatedImage.post { animationDrawable.start() }
    }
}
