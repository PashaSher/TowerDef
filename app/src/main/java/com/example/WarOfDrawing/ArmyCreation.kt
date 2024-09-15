package com.example.WarOfDrawing

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

var Points = 100
var RocketmenCounter = 0
var GunnersCounter = 0

class ArmyCreation : AppCompatActivity() {

    private lateinit var pointsTextView: TextView
    private lateinit var gunnersTextView: TextView
    private lateinit var rocketmenTextView: TextView
    private lateinit var readyButton: Button
    private lateinit var gunnerPlusButton: Button
    private lateinit var gunnerMinusButton: Button
    private lateinit var rocketmanPlusButton: Button
    private lateinit var rocketmanMinusButton: Button
    private lateinit var firebaseDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_army_creation)

        // Инициализация Firebase
        firebaseDatabase = FirebaseDatabase.getInstance().reference

        pointsTextView = findViewById(R.id.PointsView)
        gunnersTextView = findViewById(R.id.gunner_points_text)
        rocketmenTextView = findViewById(R.id.rocketman_points_text)
        readyButton = findViewById(R.id.ready)
        gunnerPlusButton = findViewById(R.id.gunner_plus_button)
        gunnerMinusButton = findViewById(R.id.gunner_minus_button)
        rocketmanPlusButton = findViewById(R.id.rocketman_plus_button)
        rocketmanMinusButton = findViewById(R.id.rocketman_minus_button)

        // Обновляем начальные значения TextView
        updatePointsText()
        updateGunnersText()
        updateRocketmenText()

        // Обработчики нажатий для увеличения/уменьшения количества стрелков
        gunnerPlusButton.setOnClickListener {
            if (Points > 0) {
                GunnersCounter++
                Points--
                updateGunnersText()
                updatePointsText()
            } else {
                Toast.makeText(this, "Недостаточно очков", Toast.LENGTH_SHORT).show()
            }
        }

        gunnerMinusButton.setOnClickListener {
            if (GunnersCounter > 0) {
                GunnersCounter--
                Points++
                updateGunnersText()
                updatePointsText()
            }
        }

        // Обработчики нажатий для увеличения/уменьшения количества ракетчиков
        rocketmanPlusButton.setOnClickListener {
            if (Points > 0) {
                RocketmenCounter++
                Points--
                updateRocketmenText()
                updatePointsText()
            } else {
                Toast.makeText(this, "Недостаточно очков", Toast.LENGTH_SHORT).show()
            }
        }

        rocketmanMinusButton.setOnClickListener {
            if (RocketmenCounter > 0) {
                RocketmenCounter--
                Points++
                updateRocketmenText()
                updatePointsText()
            }
        }

        // Логика нажатия на кнопку "Ready"
        readyButton.setOnClickListener {
            setPlayerReady()
        }

        // Следим за состоянием готовности обоих игроков
        checkPlayersReady()
    }

    // Обновляем текст с текущим количеством очков
    private fun updatePointsText() {
        pointsTextView.text = "Points: $Points"
    }

    private fun updateGunnersText() {
        gunnersTextView.text = "$GunnersCounter"
    }

    private fun updateRocketmenText() {
        rocketmenTextView.text = "$RocketmenCounter"
    }

    // Устанавливаем состояние игрока как готового в Firebase
    private fun setPlayerReady() {
        val playerRole = if (AppState.isHost) "hostReady" else "clientReady"
        firebaseDatabase.child("gameState").child(playerRole).setValue(true)
    }

    // Проверяем состояние готовности обоих игроков
    private fun checkPlayersReady() {
        firebaseDatabase.child("gameState").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val hostReady = snapshot.child("hostReady").getValue(Boolean::class.java) ?: false
                val clientReady = snapshot.child("clientReady").getValue(Boolean::class.java) ?: false

                // Проверяем, готовы ли оба игрока
                if (hostReady && clientReady) {

                    startGame()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ArmyCreation, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Запускаем игру, когда оба игрока готовы
    private fun startGame() {
        val intent = Intent(this, MapOne::class.java)
        intent.putExtra("gunnersCount", GunnersCounter)
        intent.putExtra("rocketmenCount", RocketmenCounter)
        startActivity(intent)
    }
}
