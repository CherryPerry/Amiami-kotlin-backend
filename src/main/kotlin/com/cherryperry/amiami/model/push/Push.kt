package com.cherryperry.amiami.model.push

import com.cherryperry.amiami.App
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.apache.logging.log4j.LogManager
import java.util.*

object Push {
    private const val NO_KEY = "NO_KEY"

    private val log = LogManager.getLogger(Push::class.java)!!
    private val okHttp = OkHttpClient()
    private var key = NO_KEY

    init {
        App::class.java.classLoader.getResourceAsStream("push.properties").use {
            val prop = Properties()
            prop.load(it)
            key = prop.getProperty("push.server_key", key)
        }
        if (key == NO_KEY) {
            log.error("No server key found!")
        }
    }

    public fun sendPushWithUpdatedCount(count: Int) {
        val json = Gson().toJson(PushData("/topics/updates2", PushCountData(count)))
        val request = Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .header("Authorization", "key=$key")
                .post(RequestBody.create(MediaType.parse("application/json"), json))
                .build()
        val response = okHttp.newCall(request).execute()
        if (response.isSuccessful) {
            log.info("Push notification was sent")
        } else {
            log.error("Failed to send push notification (${response.code()} - ${response.body()!!.string()}")
        }
    }
}
