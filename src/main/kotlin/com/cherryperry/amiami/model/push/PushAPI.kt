package com.cherryperry.amiami.model.push

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.CompletableFuture

interface PushAPI {

    companion object {
        const val BASE_URL = "https://fcm.googleapis.com/"
    }

    @POST("fcm/send")
    fun sendPushMessage(@Header("Authorization") accessKey: String, @Body body: PushRequest): CompletableFuture<Void>
}
