package com.cherryperry.amiami.model.currency

import com.cherryperry.amiami.util.RequestLoggingInterceptor
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate

class CurrencyRestClient(
    private val baseUrl: String = BASE_URL
) {

    companion object {
        const val BASE_URL = "http://data.fixer.io/api/"
    }

    private val restTemplate = RestTemplate()
    private val log = LogManager.getLogger(CurrencyRestClient::class.java)

    init {
        restTemplate.interceptors = listOf(RequestLoggingInterceptor(log))
    }

    fun currency(accessKey: String): CurrencyResponse {
        val headers = HttpHeaders()
        headers.accept = arrayListOf(MediaType.APPLICATION_JSON)
        val entity = HttpEntity<Void>(headers)
        val url = baseUrl + "latest?access_key=$accessKey"
        val response = restTemplate.exchange(url, HttpMethod.GET, entity,
            CurrencyResponse::class.java)
        return response.body ?: throw IllegalStateException("No body")
    }
}
