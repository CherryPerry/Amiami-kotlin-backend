package com.cherryperry.amiami.model.push

import com.cherryperry.amiami.util.readProperty
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Service
import retrofit2.Retrofit
import retrofit2.adapter.java8.Java8CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory

@Service
class PushServiceImpl constructor(
    baseUrl: String = PushAPI.BASE_URL
) : PushService {

    companion object {
        private const val PUSH_DATA_TO = "/topics/updates2"
        private const val PROPERTIES_FILE = "secure.properties"
        private const val PROPERTIES_KEY = "firebase.key"
    }

    private val log = LogManager.getLogger(PushServiceImpl::class.java)
    private val accessKey: String = readProperty(PROPERTIES_FILE) {
        getProperty(PROPERTIES_KEY, "no_key")
    }
    private val api: PushAPI

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(Java8CallAdapterFactory.create())
            .addConverterFactory(JacksonConverterFactory.create())
            .validateEagerly(true)
            .build()
        api = retrofit.create(PushAPI::class.java)
    }

    override fun sendPushWithUpdatedCount(count: Int) {
        try {
            api.sendPushMessage("key=$accessKey", PushRequest(PUSH_DATA_TO, PushCountPayload(count))).get()
            log.info("Push notification was sent")
        } catch (exception: Exception) {
            log.error("Failed to send push notification", exception.cause)
        }
    }

    override fun sendPushWithUpdateCountToDevice(count: Int, token: String) {
        try {
            api.sendPushMessage("key=$accessKey", PushRequest(token, PushCountPayload(count))).get()
            log.info("Push notification was sent")
        } catch (exception: Exception) {
            log.error("Failed to send push notification", exception.cause)
        }
    }
}
