package com.cherryperry.amiami.model.currency

import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.CompletableFuture

interface CurrencyAPI {

    companion object {
        const val BASE_URL = "http://data.fixer.io/api/"
    }

    @GET("latest")
    fun currencies(@Query("access_key") accessKey: String): CompletableFuture<CurrencyResponse>
}