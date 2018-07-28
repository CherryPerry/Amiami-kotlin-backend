package com.cherryperry.amiami.model.update

import com.cherryperry.amiami.util.RequestLoggingInterceptor
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate

class AmiamiRestClient(
    private val baseUrl: String = BASE_URL
) {

    companion object {
        const val BASE_URL = "https://api.amiami.com/api/v1.0/"
    }

    private val restTemplate = RestTemplate()
    private val log = LogManager.getLogger(AmiamiRestClient::class.java)

    init {
        restTemplate.interceptors = listOf(RequestLoggingInterceptor(log))
    }

    fun items(category: Int, perPage: Int, page: Int): AmiamiApiListResponse {
        val headers = HttpHeaders()
        headers.accept = arrayListOf(MediaType.APPLICATION_JSON)
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4")
        headers.set(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36" +
            "(KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36")
        headers.set("x-user-key", "amiami_dev")
        val entity = HttpEntity<Void>(headers)
        val url = baseUrl + "items" +
            "?s_sortkey=preowned" +
            "&s_st_condition_flg=1" +
            "&lang=eng" +
            "&s_cate_tag=$category" +
            "&pagemax=$perPage" +
            "&pagecnt=$page"
        val response = restTemplate.exchange(url, HttpMethod.GET, entity,
            AmiamiApiListResponse::class.java)
        return response.body ?: throw IllegalStateException("No body")
    }
}
