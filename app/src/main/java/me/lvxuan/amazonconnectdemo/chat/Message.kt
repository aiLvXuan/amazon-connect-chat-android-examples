package me.lvxuan.amazonconnectdemo.chat

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/**
 * @author LvXuan
 * Created by LvXuan on 2021/6/22 11:11.
 *
 * {
 *    "AbsoluteTime": "2021-06-19T09:56:18.554Z",
 *    "Content": "Send message --15",
 *    "ContentType": "text/plain",
 *    "Id": "4fe10f82-d20d-4dbd-8edc-a1967ede7852",
 *    "Type": "MESSAGE",
 *    "ParticipantId": "ced34411-415e-43b1-9d2b-ddb9237b58d0",
 *    "DisplayName": "Customer-LvXuan",
 *    "ParticipantRole": "CUSTOMER",
 *    "InitialContactId": "ee0ad51d-442e-474b-accc-1ff872c1822e",
 *    "ContactId": "ee0ad51d-442e-474b-accc-1ff872c1822e"
 * }
 *
 */
data class Message(
    @SerializedName("Content")
    var content: String,
    @SerializedName("DisplayName")
    var displayName: String,

    @SerializedName("Type")
    val type: String,
    @SerializedName("ContentType")
    val contentType: String,
    @SerializedName("ParticipantRole")
    val participantRole: String,

    @SerializedName("AbsoluteTime")
    val absoluteTime: String,
    @SerializedName("Id")
    val msgId: String,
    @SerializedName("ContactId")
    val contactId: String,
    @SerializedName("InitialContactId")
    val participantId: String,

    )


/*   {
       "content": "{\"AbsoluteTime\":\"2021-06-19T09:56:18.554Z\",\"Content\":\"Send message --15\",\"ContentType\":\"text/plain\",\"Id\":\"4fe10f82-d20d-4dbd-8edc-a1967ede7852\",\"Type\":\"MESSAGE\",\"ParticipantId\":\"ced34411-415e-43b1-9d2b-ddb9237b58d0\",\"DisplayName\":\"Customer-LvXuan\",\"ParticipantRole\":\"CUSTOMER\",\"InitialContactId\":\"ee0ad51d-442e-474b-accc-1ff872c1822e\",\"ContactId\":\"ee0ad51d-442e-474b-accc-1ff872c1822e\"}",
       "contentType": "application/json",
       "topic": "aws/chat"
   }*/
data class MessageWrap(
    val content: String,
    val contentType: String,
    val topic: String
)

object MessageHandle {
    fun handle(gson: Gson, result: String): Message {
        val wrap = gson.fromJson(result, MessageWrap::class.java)
        return gson.fromJson(wrap.content, Message::class.java)
    }
}

