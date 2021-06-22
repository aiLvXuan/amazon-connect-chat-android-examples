package me.lvxuan.amazonconnectdemo.chat

import android.util.Log
import com.google.gson.Gson
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

/**
 * @author LvXuan
 * Created by LvXuan on 2021/6/11 17:55.
 */
class WebSocketManager(wsUrl: String, val onReceivedMessage: (Message) -> Unit = {}) :
    WebSocketClient(URI.create(wsUrl)) {

    private val gson = Gson()
    private val TAG = "WebSocketManager"

    override fun onOpen(handshakedata: ServerHandshake?) {
        send("""{"topic": "aws/subscribe", "content": {"topics": ["aws/chat"]}})""")
    }

    override fun onMessage(message: String?) {
        Log.i(TAG, "onMessage: $message")

        if (message.isNullOrEmpty()) return
        val msg = MessageHandle.handle(gson, message)
        when {

            msg.type == "MESSAGE" -> {
                onReceivedMessage(msg)
            }

            msg.contentType == "application/vnd.amazonaws.connect.event.chat.ended" -> {
                msg.content = "The chat has ended."
                msg.displayName = "System message"
                onReceivedMessage(msg)
            }

            msg.contentType == "application/vnd.amazonaws.connect.event.participant.joined" -> {
                msg.content = "The customer service has joined."
                msg.displayName = "System message"
            }
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.i(TAG, "onClose: code:$code   -reason:$reason   -remote:$remote")
    }

    override fun onError(ex: Exception?) {
        Log.i(TAG, "onError: --> $ex")
    }
}