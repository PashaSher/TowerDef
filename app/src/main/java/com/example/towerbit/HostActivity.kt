package com.example.towerbit

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)
        // Полноэкранный режим и скрытие системных панелей
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

            waitButton.text = "server started"
            waitButton.isEnabled = false
            startServer()

        }
    }

    // Функция для запуска сервера и ожидания клиента
    private fun startServer() {
        serverRunning = true
        serverThread = thread(start = true) {
            try {
                val serverSocket = java.net.ServerSocket(serverPort)
                Log.d("Server", "Сервер запущен, ожидает подключения на порту $serverPort")

                while (serverRunning) {
                    val clientSocket = serverSocket.accept()
                    Log.d("Server", "Клиент подключен: ${clientSocket.inetAddress}")
                    handleClient(clientSocket)
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

                clientSocket.close()
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
