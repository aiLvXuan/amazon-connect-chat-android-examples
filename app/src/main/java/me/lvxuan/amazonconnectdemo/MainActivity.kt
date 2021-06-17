package me.lvxuan.amazonconnectdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import me.lvxuan.amazonconnectdemo.chat.ChatWrapper
import me.lvxuan.amazonconnectdemo.databinding.ActivityMainBinding
import me.lvxuan.amazonconnectdemo.chat.WebSocketManager
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding
    private var count = 0
    private val message
        get() = "Send message --${count++}"

    private var websocketManager: WebSocketManager? = null
    private val chatWrapper = ChatWrapper()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStartChat.setOnClickListener {
            // TODO: 2021/6/17 Use Kotlin-Coroutine
            thread {
                chatWrapper.startChatContact("Customer-LvXuan")
                chatWrapper.createParticipantConnection()

                val wsUrl = chatWrapper.websocketUrl!!
                websocketManager = WebSocketManager(wsUrl) { msg ->
                    Log.i(TAG, "Received message: $msg")
                }
            }

        }

        binding.btnSendMessage.setOnClickListener {
            // TODO: 2021/6/17 Use Kotlin-Coroutine
            thread {
                val msg = message
                chatWrapper.sendChatMessage(msg)
                Log.i(TAG, "Send message: $msg")
            }
        }

        binding.btnEndChat.setOnClickListener {
            // TODO: 2021/6/17 Use Kotlin-Coroutine
            thread {
                chatWrapper.endChat()
                Log.i(TAG, "End Chat...")
            }
        }
    }


}