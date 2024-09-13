package com.example.towerbit

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import kotlin.concurrent.thread

class ClientActivity : AppCompatActivity() {

    private lateinit var ipPinInput: EditText
    private lateinit var connectButton: Button
    private var clientSocket: Socket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN       // Полноэкранный режим (скрытие статусной строки)
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Скрытие навигационной панели
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) // Восстановление системных панелей при свайпе
        ipPinInput = findViewById(R.id.ip_pin_input)
        connectButton = findViewById(R.id.connect_button)

        // Получаем данные из буфера обмена
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipboard.primaryClip
        if (clipData != null && clipData.itemCount > 0) {
            val copiedText = clipData.getItemAt(0).text.toString()
            ipPinInput.setText(copiedText)  // Вставляем скопированный IP и PIN
        }

        // Логика подключения к хосту при нажатии на кнопку "Connect"
        connectButton.setOnClickListener {
            val inputText = ipPinInput.text.toString()

            if (inputText.isNotEmpty()) {
                val (ipAddress, pinCode) = parseIpPin(inputText)

                if (ipAddress != null && pinCode != null) {
                    connectToHost(ipAddress, pinCode)
                } else {
                    Toast.makeText(this, "Invalid IP or PIN format", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "IP and PIN must be provided", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Функция для парсинга строки IP и PIN
    private fun parseIpPin(input: String): Pair<String?, String?> {
        val parts = input.split("\n")
        return if (parts.size == 2) {
            val ipPart = parts[0].replace("IP: ", "").trim()
            val pinPart = parts[1].replace("PIN: ", "").trim()
            Log.d("ClientActivity", "Parsed IP: $ipPart, PIN: $pinPart")
            Pair(ipPart, pinPart)
        } else {
            Log.d("ClientActivity", "Failed to parse IP and PIN")
            Pair(null, null)
        }
    }

    // Логика подключения к хосту
    private fun connectToHost(ipAddress: String, pinCode: String) {
        thread {
            try {
                Log.d("ClientActivity", "Connecting to $ipAddress on port 8080 with PIN $pinCode")
                clientSocket = Socket(ipAddress, 8080) // Подключение к серверу на порту 8080
                val input = BufferedReader(InputStreamReader(clientSocket!!.getInputStream()))
                val output = PrintWriter(clientSocket!!.getOutputStream(), true)

                // Отправляем PIN на сервер для проверки
                output.println(pinCode)

                // Читаем ответ от сервера
                var response: String?
                while (clientSocket!!.isConnected) {
                    response = input.readLine()

                    if (response != null) {
                        Log.d("ClientActivity", "Server response: $response")

                        // Если сервер отправляет команду "START_GAME"
                        if (response == "START_GAME") {
                            runOnUiThread {
                                Toast.makeText(this@ClientActivity, "Game starting!", Toast.LENGTH_SHORT).show()

                                // Изменяем активность и переходим в новую
                                val intent = Intent(this@ClientActivity, ArmyCreation::class.java)
                                startActivity(intent)
                            }

                            // Прерываем цикл, так как мы получили нужное сообщение
                            break
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("ClientActivity", "Connection error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@ClientActivity, "Connection failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
