package com.cherryperry.amiami.model.push

import com.cherryperry.amiami.util.readProperty
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class PushServiceImpl constructor(
    private val baseUrl: String = BASE_URL
) : PushService {

    companion object {
        private const val PUSH_DATA_TO = "/topics/updates2"
        private const val PROPERTIES_FILE = "secure.properties"
        private const val PROPERTIES_KEY = "firebase.key"
        private const val BASE_URL = "https://fcm.googleapis.com/"
    }

    private val log = LogManager.getLogger(PushServiceImpl::class.java)
    private val accessKey: String = readProperty(PROPERTIES_FILE) {
        getProperty(PROPERTIES_KEY, "no_key")
    }
    private val restTemplate = RestTemplate()

    override fun sendPushWithUpdatedCount(count: Int) {
        try {
            sendPushMessage(PushRequest(PUSH_DATA_TO, PushCountPayload(count)))
            log.info("Push notification was sent")
        } catch (exception: Exception) {
            log.error("Failed to send push notification", exception)
        }
    }

    override fun sendPushWithUpdateCountToDevice(count: Int, token: String) {
        try {
            sendPushMessage(PushRequest(token, PushCountPayload(count)))
            log.info("Push notification was sent")
        } catch (exception: Exception) {
            log.error("Failed to send push notification", exception)
        }
    }

    private fun sendPushMessage(pushRequest: PushRequest) {
        val headers = HttpHeaders()
        headers.accept = arrayListOf(MediaType.APPLICATION_JSON)
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set(HttpHeaders.AUTHORIZATION, "key=$accessKey")
        val entity = HttpEntity(pushRequest, headers)
        restTemplate.exchange(baseUrl + "fcm/send", HttpMethod.POST, entity, Void::class.java)
    }
}
