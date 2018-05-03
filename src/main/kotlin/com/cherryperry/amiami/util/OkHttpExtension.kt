package com.cherryperry.amiami.util

import okhttp3.OkHttpClient
import okhttp3.Request

fun OkHttpClient.getString(url: String): String {
    val request = Request.Builder().url(url).get().build()
    val response = this.newCall(request).execute()
    if (response.isSuccessful) {
        return response.body()!!.string()
    }
    throw IllegalStateException("Response is not successful (${response.code()})")
}