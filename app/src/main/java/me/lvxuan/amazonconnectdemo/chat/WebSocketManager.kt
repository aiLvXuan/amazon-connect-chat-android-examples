package me.lvxuan.amazonconnectdemo.chat

import android.util.Log
import com.google.gson.JsonParser
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI

/**
 * @author LvXuan
 * Created by LvXuan on 2021/6/11 17:55.
 */
class WebSocketManager(wsUrl:String, val onReceivedMessage: (Message) -> Unit = {}) :
    WebSocketClient(URI.create(wsUrl)) {

    private val TAG = "WebSocketManager"

    override fun onOpen(handshakedata: ServerHandshake?) {
        Log.i(TAG, "onOpen: ")
    }

    override fun onMessage(message: String?) {
        Log.i(TAG, "onMessage: $message")
//     todo    onReceivedMessage(message)

        val jsonObject = JsonParser.parseString(message).asJsonObject
        val type = jsonObject.get("Type").asString
        if (type == "MESSAGE") {
            val participantRole = jsonObject.get("ParticipantRole").asString
            val text = jsonObject.get("Content").asString
            val msg = Message(participantRole, text)
            onReceivedMessage(msg)
        } else if (type == "application/vnd.amazonaws.connect.event.chat.ended") {
            onReceivedMessage(Message("System Message", "The chat has ended."))
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.i(TAG, "onClose: code:$code   -reason:$reason   -remote:$remote")
    }

    override fun onError(ex: Exception?) {
        Log.i(TAG, "onError: --> $ex")
    }
}