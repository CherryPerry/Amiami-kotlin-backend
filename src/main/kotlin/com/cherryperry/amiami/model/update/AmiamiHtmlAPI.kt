package com.cherryperry.amiami.model.update

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Url

interface AmiamiHtmlAPI {

    @GET
    @Headers(value = [
        "Accept-Language: ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4",
        "User-Agent: Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36" +
            "(KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36"
    ])
    fun htmlPage(@Url url: String): Single<String>
}
