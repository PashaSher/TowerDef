package com.example.WarOfDrawing

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import kotlin.concurrent.thread


class HostActivity : AppCompatActivity() {

    private lateinit var ipPinText: TextView
    private lateinit var copyButton: Button
    private lateinit var waitButton: Button
    private lateinit var collapseButton: Button
    private val serverPort = 8080
    private lateinit var serverThread: Thread
    private var serverRunning = false
    private var startGameFlag = false

    // Добавляем глобальную переменную для клиентского сокета
    private var clientSocket: java.net.Socket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN       // Полноэкранный режим (скрытие статусной строки)
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Скрытие навигационной панели
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) // Восстановление системных панелей при свайпе


        ipPinText = findViewById(R.id.ip_pin_text)
        copyButton = findViewById(R.id.copy_button)
        waitButton = findViewById(R.id.wait_button)
        collapseButton = findViewById(R.id.collapse_button)

        // Получаем IP-адрес устройства
        val ipAddress = getLocalIpAddress()
        AppState.isHost = true
        // Генерируем случайный PIN-код
        val pinCode = (1000..9999).random()

        // Отображаем IP-адрес и PIN-код
        val ipPinInfo = "IP: $ipAddress\nPIN: $pinCode"
        ipPinText.text = ipPinInfo

        // Копируем в буфер обмена при нажатии на кнопку
        copyButton.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("IP и PIN", ipPinInfo)
            clipboard.setPrimaryClip(clip)
            Log.d("HostActivity", "IP и PIN скопированы в буфер обмена")
            copyButton.text = "COPIED"
        }

        collapseButton.setOnClickListener {
            moveTaskToBack(true)
        }

        // Запускаем сервер при нажатии на кнопку ожидания клиента
        waitButton.setOnClickListener {
            waitButton.text = "Server started"
            waitButton.isEnabled = false
            if(!startGameFlag) {
                startServer()
            }
            else {
                startGameFlag = false

                // Отправляем клиенту сообщение о начале игры
                thread {
                    try {
                        val output = java.io.PrintWriter(clientSocket!!.getOutputStream(), true)
                        output.println("START_GAME")
                        Log.d("Server", "Сообщение отправлено: START_GAME")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                // Запуск новой активности на стороне хоста
                val intent = Intent(this, ArmyCreation::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.flip_in, R.anim.flip_out)
            }
        }
    }

    // Функция для запуска сервера и ожидания клиента
    private fun startServer() {
        serverRunning = true
        serverThread = thread(start = true) {
            try {
                val serverSocket = java.net.ServerSocket(serverPort)
                Log.d("Server", "Сервер запущен, ожидает подключения на порту $serverPort")

                // Сообщение о запуске сервера
                runOnUiThread {
                    Toast.makeText(this, "Server started on port $serverPort", Toast.LENGTH_SHORT).show()
                }

                while (serverRunning) {
                    clientSocket = serverSocket.accept()
                    val clientAddress = clientSocket!!.inetAddress.hostAddress

                    // Сообщение о подключении клиента
                    runOnUiThread {
                        Toast.makeText(this, "Client connected: $clientAddress", Toast.LENGTH_SHORT).show()
                        waitButton.text = "Start Game"
                        waitButton.isEnabled = true
                        startGameFlag = true
                    }

                    Log.d("Server", "Клиент подключен: $clientAddress")
                    handleClient(clientSocket!!)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Функция для обработки клиента
    private fun handleClient(clientSocket: java.net.Socket) {
        thread {
            try {
                val input = java.io.BufferedReader(java.io.InputStreamReader(clientSocket.getInputStream()))
                val output = java.io.BufferedWriter(java.io.OutputStreamWriter(clientSocket.getOutputStream()))

                var message: String?
                while (clientSocket.isConnected) {
                    message = input.readLine()
                    if (message != null) {
                        Log.d("Server", "Сообщение от клиента: $message")
                        output.write("Сообщение получено: $message\n")
                        output.flush()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Функция для получения IP-адреса устройства
    private fun getLocalIpAddress(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (!address.isLoopbackAddress && address is Inet4Address) {
                        return address.hostAddress
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        // Останавливаем сервер при уничтожении активности
        serverRunning = false
    }
}
