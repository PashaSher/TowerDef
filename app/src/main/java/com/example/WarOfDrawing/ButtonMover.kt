package com.example.WarOfDrawing

import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import kotlin.math.sqrt

class ButtonMover(private val button: ImageButton) {

    // Waypoints (точки, по которым перемещаем кнопку)
    private var waypoints: List<Pair<Float, Float>> = emptyList()
    private var currentWaypointIndex = 0

    // Текущие координаты
    private var posX = button.x
    private var posY = button.y

    // Скорость перемещения
    private val speed = 5f

    // Handler для обновления позиции
    private val handler = Handler(Looper.getMainLooper())

    // Флаг для остановки движения
    private var isMoving = true

    // Устанавливаем waypoints для движения
    fun setWaypoints(waypoints: List<Pair<Float, Float>>) {
        this.waypoints = waypoints
        this.currentWaypointIndex = 0 // Начинаем с первой точки
    }

    // Движение к следующей точке
    private fun moveToNextWaypoint() {
        if (currentWaypointIndex < waypoints.size) {
            val (targetX, targetY) = waypoints[currentWaypointIndex]

            // Простое перемещение по X и Y к следующей точке
            val deltaX = targetX - posX
            val deltaY = targetY - posY
            val distance = sqrt((deltaX * deltaX + deltaY * deltaY).toDouble()).toFloat()

            if (distance > speed) {
                posX += (deltaX / distance) * speed
                posY += (deltaY / distance) * speed
            } else {
                posX = targetX
                posY = targetY
                currentWaypointIndex++
            }

            // Перемещаем кнопку (ImageButton)
            button.x = posX
            button.y = posY
        }
    }

    // Запуск движения
    fun startMoving() {
        handler.post(object : Runnable {
            override fun run() {
                if (isMoving) {
                    moveToNextWaypoint()  // Двигаем к следующей точке

                    // Повторяем каждые 30 миллисекунд
                    handler.postDelayed(this, 30)
                }
            }
        })
    }

    // Остановка движения
    fun stopMoving() {
        isMoving = false
        handler.removeCallbacksAndMessages(null)
    }
}
