package me.lvxuan.amazonconnectdemo.chat

import androidx.annotation.WorkerThread
import com.amazonaws.services.connect.AmazonConnect
import com.amazonaws.services.connect.AmazonConnectClient
import com.amazonaws.services.connect.model.ParticipantDetails
import com.amazonaws.services.connect.model.StartChatContactRequest
import com.amazonaws.services.connect.model.StopContactRequest
import com.amazonaws.services.connectparticipant.AmazonConnectParticipant
import com.amazonaws.services.connectparticipant.AmazonConnectParticipantClient
import com.amazonaws.services.connectparticipant.model.CreateParticipantConnectionRequest
import com.amazonaws.services.connectparticipant.model.SendEventRequest
import com.amazonaws.services.connectparticipant.model.SendMessageRequest

/**
 * @author LvXuan
 * Created by LvXuan on 2021/6/15 11:47.
 */
class ChatWrapper {
    companion object {
        const val INSTANCE_ID = "INSTANCE_ID"   // TODO - fill in
        const val CONTACT_FLOW_ID = "CONNECT_FLOW_ID"  // TODO - fill in
    }

    val connectServiceClient: AmazonConnect
    val connectChatClient: AmazonConnectParticipant
    var participantToken: String? = null
    var connectionToken: String? = null
    var contactId: String? = null
    var websocketUrl: String? = null

    val accessKey = "ACCESS_KEY_ID"   // TODO - fill in  (??? fetching from the server)
    val secretKey = "SECRET_ACCESS_KEY_ID"    // TODO - fill in  (??? fetching from the server)

    var isStart = false

    init {
        val credentials = AWSStaticCredentialsProvider(accessKey, secretKey)

        connectServiceClient = AmazonConnectClient(credentials)
        connectChatClient = AmazonConnectParticipantClient()
//        https://docs.aws.amazon.com/zh_cn/general/latest/gr/connect_region.html
//        connectChatClient.setRegion(Region.getRegion("todo1"))
    }

    @WorkerThread
    fun startChatContact(name: String) {
        val startChatContactRequest = StartChatContactRequest()
            .withInstanceId(INSTANCE_ID)
            .withContactFlowId(CONTACT_FLOW_ID)
            .withParticipantDetails(ParticipantDetails().withDisplayName(name))
            .withAttributes(mapOf("customerName" to name, "username" to "username"))

        val result = connectServiceClient.startChatContact(startChatContactRequest)

        this.participantToken = result.participantToken
        this.contactId = result.contactId
        isStart = true

    }

    @WorkerThread
    fun createParticipantConnection() {
        val createParticipantConnectionRequest = CreateParticipantConnectionRequest()
            .withParticipantToken(this.participantToken)
            .withType("WEBSOCKET", "CONNECTION_CREDENTIALS")

        val result = connectChatClient
            .createParticipantConnection(createParticipantConnectionRequest)
        this.connectionToken = result.connectionCredentials.connectionToken
        this.websocketUrl = result.websocket.url
    }

    @WorkerThread
    fun sendChatMessage(message: String) {
        val sendMessageRequest = SendMessageRequest()
            .withClientToken(connectionToken)
            .withContent(message)
            .withContentType("text/plain")

        connectChatClient.sendMessage(sendMessageRequest)
    }

    @WorkerThread
    fun sendTypingEvent() {
        val sendEventRequest = SendEventRequest()
            .withConnectionToken(this.connectionToken)
            .withContentType("application/vnd.amazonaws.connect.event.typing")

        connectChatClient.sendEvent(sendEventRequest)

    }

    @WorkerThread
    fun endChat() {
        val stopChatRequest = StopContactRequest()
            .withInstanceId(INSTANCE_ID)
            .withContactId(this.contactId)

        connectServiceClient.stopContact(stopChatRequest)
        isStart = false
    }
}