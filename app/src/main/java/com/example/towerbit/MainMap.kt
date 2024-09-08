package com.example.towerbit

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.hypot

class MainMap : AppCompatActivity() {

    // Зададим радиус действия вышек
    private val towerRange = 3500f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_map)

        val mainLayout = findViewById<ConstraintLayout>(R.id.main)

        // Получаем ImageView для зомби
        val zombie1 = findViewById<ImageView>(R.id.red_dot1)
        val zombie2 = findViewById<ImageView>(R.id.red_dot2)

        // Получаем Button для вышек
        val tower1 = findViewById<Button>(R.id.tower_spot1)
        val tower2 = findViewById<Button>(R.id.tower_spot2)

        // Анимация перемещения зомби
        animateImage(zombie1, 3000f, 50000)
        animateImage(zombie2, 3000f, 40000)

        // Обработка выстрелов вышек
        startTowerShooting(tower1, zombie1)
        startTowerShooting(tower2, zombie2)

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Анимация перемещения изображения по вертикали
    private fun animateImage(image: ImageView, distance: Float, duration: Long) {
        val animator = ObjectAnimator.ofFloat(image, "translationY", distance)
        animator.duration = duration
        animator.repeatCount = ObjectAnimator.INFINITE
        animator.repeatMode = ObjectAnimator.RESTART
        animator.start()
    }

    // Проверка, находится ли зомби в радиусе действия вышки
    private fun isZombieInRange(tower: Button, zombie: ImageView): Boolean {
        val towerLocation = IntArray(2)
        val zombieLocation = IntArray(2)

        tower.getLocationOnScreen(towerLocation)
        zombie.getLocationOnScreen(zombieLocation)

        val dx = (zombieLocation[0] - towerLocation[0]).toFloat()
        val dy = (zombieLocation[1] - towerLocation[1]).toFloat()
        val distance = hypot(dx, dy)

        return distance <= towerRange
    }

    // Запуск стрельбы вышки по зомби
    private fun startTowerShooting(tower: Button, zombie: ImageView) {
        val handler = Handler(Looper.getMainLooper())

        handler.post(object : Runnable {
            override fun run() {
                if (isZombieInRange(tower, zombie)) {
                    shootAtZombie(tower, zombie)
                }
                handler.postDelayed(this, 10000) // Проверяем радиус и стреляем каждую секунду
            }
        })
    }

    // Анимация стрельбы по зомби
    private fun shootAtZombie(tower: Button, zombie: ImageView) {
        val bullet = ImageView(this)
        bullet.setImageResource(R.drawable.ic_launcher_foreground) // Убедитесь, что у вас есть изображение пули в drawable
        bullet.layoutParams = ConstraintLayout.LayoutParams(20, 20) // Размер пули

        val mainLayout = findViewById<ConstraintLayout>(R.id.main)
        mainLayout.addView(bullet)

        // Начальная позиция пули (позиция вышки)
        bullet.x = tower.x + tower.width / 2
        bullet.y = tower.y + tower.height / 2

        // Анимация перемещения пули к зомби
        val bulletAnimatorX = ObjectAnimator.ofFloat(bullet, "translationX", zombie.x + zombie.width / 2)
        val bulletAnimatorY = ObjectAnimator.ofFloat(bullet, "translationY", zombie.y + zombie.height / 2)

        bulletAnimatorX.duration = 5000
        bulletAnimatorY.duration = 5000

        bulletAnimatorX.start()
        bulletAnimatorY.start()

        bulletAnimatorY.doOnEnd {
            // Удаляем пулю после попадания
            mainLayout.removeView(bullet)
            // Здесь можно добавить урон для зомби
            Toast.makeText(this, "Попадание по зомби!", Toast.LENGTH_SHORT).show()
        }
    }
}
