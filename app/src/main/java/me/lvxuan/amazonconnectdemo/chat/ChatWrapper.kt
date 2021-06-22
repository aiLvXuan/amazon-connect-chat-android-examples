package me.lvxuan.amazonconnectdemo.chat

import androidx.annotation.WorkerThread
import com.amazonaws.AmazonServiceException
import com.amazonaws.regions.Region
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
    private val TAG = "ChatWrapper"

    companion object {

        const val INSTANCE_ID = "INSTANCE_ID"   // TODO - fill in

        /**
         * CONTACT_FLOW_ID
         * see: [StartChatContactRequest.withContactFlowId]
         * doc: https://docs.aws.amazon.com/connect/latest/adminguide/tutorial1-create-contact-flow.html
         */
        const val CONTACT_FLOW_ID = "CONTACT_FLOW_ID"  // TODO - fill in

        /**
         * ACCESS_KEY, SECRET_KEY
         * home: https://console.aws.amazon.com/iam/
         * Access management --> Users --> Add user
         * or  Access management --> Users --> "user" --> Security credentials -->  Create access key
         */
        const val ACCESS_KEY = "ACCESS_KEY"   // TODO - fill in
        const val SECRET_KEY = "SECRET_KEY"    // TODO - fill in
    }

    val connectServiceClient: AmazonConnect
    val connectChatClient: AmazonConnectParticipant
    var participantToken: String? = null
    var connectionToken: String? = null
    var contactId: String? = null
    var websocketUrl: String? = null


    init {
        val credentials = AWSStaticCredentialsProvider(ACCESS_KEY, SECRET_KEY)

        connectServiceClient = AmazonConnectClient(credentials)
        connectChatClient = AmazonConnectParticipantClient()

//        https://docs.aws.amazon.com/zh_cn/general/latest/gr/connect_region.html
        connectServiceClient.setRegion(Region.getRegion("ap-southeast-1"))
        connectChatClient.setRegion(Region.getRegion("ap-southeast-1"))

    }

    @Throws(AmazonServiceException::class)
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

    }

    @Throws(AmazonServiceException::class)
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

    @Throws(AmazonServiceException::class)
    @WorkerThread
    fun sendChatMessage(message: String) {
        val sendMessageRequest = SendMessageRequest()
            .withConnectionToken(connectionToken)
            .withContent(message)
            .withContentType("text/plain")

        connectChatClient.sendMessage(sendMessageRequest)
    }

    @Throws(AmazonServiceException::class)
    @WorkerThread
    fun sendTypingEvent() {
        val sendEventRequest = SendEventRequest()
            .withConnectionToken(this.connectionToken)
            .withContentType("application/vnd.amazonaws.connect.event.typing")

        connectChatClient.sendEvent(sendEventRequest)

    }

    @Throws(AmazonServiceException::class)
    @WorkerThread
    fun endChat() {
        val stopChatRequest = StopContactRequest()
            .withInstanceId(INSTANCE_ID)
            .withContactId(this.contactId)

        connectServiceClient.stopContact(stopChatRequest)
    }
}