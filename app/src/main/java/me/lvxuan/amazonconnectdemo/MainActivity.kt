package me.lvxuan.amazonconnectdemo

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.AmazonServiceException
import me.lvxuan.amazonconnectdemo.chat.ChatWrapper
import me.lvxuan.amazonconnectdemo.chat.WebSocketManager
import me.lvxuan.amazonconnectdemo.databinding.ActivityMainBinding
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding

    private var websocketManager: WebSocketManager? = null
    private val chatWrapper = ChatWrapper()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvMsg.movementMethod = ScrollingMovementMethod.getInstance()

        binding.btnStartChat.setOnClickListener {
            // TODO: 2021/6/17 Use Kotlin-Coroutine
            thread {
                try {
                    chatWrapper.startChatContact("Customer-LvXuan")
                    chatWrapper.createParticipantConnection()

                    val wsUrl = chatWrapper.websocketUrl!!

                    websocketManager = WebSocketManager(wsUrl) { msg ->
                        Log.i(TAG, "Received message: $msg")
                        runOnUiThread {
                            binding.tvMsg.append("\n${msg.displayName}: ${msg.content}")
                        }
                    }
                    websocketManager?.connect()

                } catch (e: AmazonServiceException) {
                    Log.e(TAG, "Chat connect error", e)
                }
            }

        }

        binding.btnSendMessage.setOnClickListener {
            // TODO: 2021/6/17 Use Kotlin-Coroutine
            thread {
                val msg = binding.etMsg.text.toString().also { if (it.isEmpty()) return@thread }
                try {
                    chatWrapper.sendChatMessage(msg)
                } catch (e: AmazonServiceException) {
                    Log.e(TAG, "Send message error: ", e)
                }
            }
        }

        binding.btnEndChat.setOnClickListener {
            // TODO: 2021/6/17 Use Kotlin-Coroutine
            thread {
                chatWrapper.endChat()
            }
        }
    }


    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            websocketManager?.close()
        }
    }

}