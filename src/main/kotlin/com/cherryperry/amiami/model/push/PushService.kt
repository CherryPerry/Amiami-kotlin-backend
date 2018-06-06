package com.cherryperry.amiami.model.push

interface PushService {

    fun sendPushWithUpdatedCount(count: Int)

    fun sendPushWithUpdateCountToDevice(count: Int, token: String)
}