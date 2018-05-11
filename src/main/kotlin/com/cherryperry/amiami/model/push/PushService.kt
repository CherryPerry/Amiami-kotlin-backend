package com.cherryperry.amiami.model.push

import com.cherryperry.amiami.App
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Service
import retrofit2.Retrofit
import retrofit2.adapter.java8.Java8CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

@Service
open class PushService constructor(
        baseUrl: String = PushAPI.BASE_URL
) {

    companion object {
        private const val PUSH_DATA_TO = "/topics/updates2"
    }

    private val log = LogManager.getLogger(PushService::class.java)
    private val api: PushAPI
    private val accessKey: String

    init {
        accessKey = App::class.java.classLoader.getResourceAsStream("push.properties").use {
            val properties = Properties()
            properties.load(it)
            properties.getProperty("push.server_key", "no_key")
        }
        val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(Java8CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .validateEagerly(true)
                .build()
        api = retrofit.create(PushAPI::class.java)
    }

    fun sendPushWithUpdatedCount(count: Int) {
        try {
            api.sendPushMessage(accessKey, PushRequest(PUSH_DATA_TO, PushCountPayload(count))).get()
            log.info("Push notification was sent")
        } catch (exception: Exception) {
            log.error("Failed to send push notification", exception.cause)
        }
    }
}