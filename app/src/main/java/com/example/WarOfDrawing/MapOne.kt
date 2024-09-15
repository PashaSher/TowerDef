package com.example.WarOfDrawing

import android.graphics.BitmapFactory
import android.graphics.Path
import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import com.example.WarOfDrawing.characters.Gunner
import com.example.WarOfDrawing.characters.Rocketman
import com.example.WarOfDrawing.characters.CharacterPresets
import com.example.WarOfDrawing.movement.MovementDrawer

class MapOne : AppCompatActivity(), SurfaceHolder.Callback {
    private lateinit var surfaceView: SurfaceView
    private val characters = mutableListOf<com.example.WarOfDrawing.characters.Character>()
    private var selectedCharacter: com.example.WarOfDrawing.characters.Character? = null
    private val path = Path()
    private var isDrawing = false
    private lateinit var movementDrawer: MovementDrawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_one)

        // Загрузка изображений персонажей
        val gunnerBitmap = BitmapFactory.decodeResource(resources, R.drawable.guner_black)
        val rocketmanBitmap = BitmapFactory.decodeResource(resources, R.drawable.rocketman_black_right)

        // Получение данных о персонажах
        val gunnersCount = intent.getIntExtra("gunnersCount", 0)
        val rocketmenCount = intent.getIntExtra("rocketmenCount", 0)

        // Инициализация персонажей с хардкорными характеристиками
        for (i in 1..gunnersCount) {
            characters.add(
                Gunner(
                    "gunner_$i",
                    "Gunner $i",
                    50f,
                    100f + i * 150f,
                    gunnerBitmap,
                    CharacterPresets.getGunnerAttributes()
                )
            )
        }
        for (i in 1..rocketmenCount) {
            characters.add(
                Rocketman(
                    "rocketman_$i",
                    "Rocketman $i",
                    50f,
                    200f + i * 150f,
                    rocketmanBitmap,
                    CharacterPresets.getRocketmanAttributes()
                )
            )
        }

        // Инициализация SurfaceView и ожидание готовности холста
        surfaceView = findViewById(R.id.surfaceView)
        val surfaceHolder = surfaceView.holder
        surfaceHolder.addCallback(this)

        // Инициализация MovementDrawer для отрисовки
        movementDrawer = MovementDrawer(surfaceView, characters)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // Холст готов, отрисуем начальные персонажи
        movementDrawer.drawCharacters()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {}

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Проверка касания персонажа
                    characters.forEach { character ->
                        if (Math.abs(it.x - character.x) < 50 && Math.abs(it.y - character.y) < 50) {
                            selectedCharacter = character
                        }
                    }

                    // Используем оператор if для выполнения действия, а не выражения
                    if (selectedCharacter != null) {
                        path.reset()
                        path.moveTo(event.x, event.y)
                        isDrawing = true
                    } else {
                        isDrawing = false
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (isDrawing) {
                        path.lineTo(it.x, it.y)
                        movementDrawer.drawPath(path)
                    } else {
                        isDrawing = false
                    }
                }
                MotionEvent.ACTION_UP -> {
                    isDrawing = false
                    // Логика для перемещения персонажа по траектории
                    selectedCharacter?.let { character ->
                        movementDrawer.moveCharacterAlongPath(character, path)
                    }
                }
                else -> {
                    // Обработка остальных действий, если это нужно
                    isDrawing = false
                }
            }
        }
        return true
    }

}
