package com.cherryperry.amiami.model.push

data class PushRequest(
    val to: String,
    val data: PushCountPayload
)