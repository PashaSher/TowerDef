package com.example.WarOfDrawing.movement

import android.animation.ValueAnimator
import android.graphics.*
import android.view.SurfaceView
import com.example.WarOfDrawing.characters.Character

class MovementDrawer(
    private val surfaceView: SurfaceView,
    private val characters: List<Character>
) {

    private val paint: Paint = Paint().apply {
        strokeWidth = 5f
        color = Color.RED
        style = Paint.Style.STROKE
    }

    // Предварительно масштабированные изображения персонажей
    private val resizedBitmaps = characters.map { character ->
        Bitmap.createScaledBitmap(
            character.bitmap,
            character.attributes.width.toInt(),
            character.attributes.height.toInt(),
            true
        )
    }

    // Метод для отрисовки персонажей
    private fun drawCharactersOnCanvas(canvas: Canvas) {
        characters.forEachIndexed { index, character ->
            val resizedBitmap = resizedBitmaps[index]

            // Отрисовываем с учетом новых размеров
            canvas.drawBitmap(
                resizedBitmap,
                character.x - character.attributes.width / 2,
                character.y - character.attributes.height / 2,
                paint
            )
        }
    }

    // Метод для отрисовки персонажей
    fun drawCharacters() {
        val canvas = surfaceView.holder.lockCanvas()
        canvas?.let {
            it.drawColor(Color.WHITE) // Очистка холста перед отрисовкой
            drawCharactersOnCanvas(it)
            surfaceView.holder.unlockCanvasAndPost(it)
        }
    }

    // Метод для отрисовки траектории движения
    fun drawPath(path: Path) {
        val canvas = surfaceView.holder.lockCanvas()
        canvas?.let {
            it.drawColor(Color.WHITE) // Очистка холста перед отрисовкой траектории
            it.drawPath(path, paint)
            drawCharactersOnCanvas(it) // Повторная отрисовка персонажей после пути
            surfaceView.holder.unlockCanvasAndPost(it)
        }
    }

    // Метод для перемещения персонажа вдоль траектории
    fun moveCharacterAlongPath(character: Character, path: Path) {
        // Используем PathMeasure для вычисления положения персонажа вдоль траектории
        val pathMeasure = PathMeasure(path, false)
        val pathLength = pathMeasure.length

        // Анимация перемещения персонажа по траектории
        val animator = ValueAnimator.ofFloat(0f, pathLength).apply {
            duration = 3000 // Длительность анимации (3 секунды)
            addUpdateListener { animation ->
                val distance = animation.animatedValue as Float
                val position = FloatArray(2)
                pathMeasure.getPosTan(distance, position, null)

                // Обновляем позицию персонажа
                character.x = position[0]
                character.y = position[1]

                // Перерисовываем персонажа
                drawCharacters()
            }
        }
        animator.start()
    }
}
