package com.cherryperry.amiami.util

import org.apache.logging.log4j.Logger
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

class RequestLoggingInterceptor(
    private val log: Logger
) : ClientHttpRequestInterceptor {

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        log.info("HTTP request ${request.method} ${request.uri} ${request.headers}")
        val response = execution.execute(request, body)
        log.info("HTTP response ${response.statusCode} ${response.statusText}")
        return response
    }
}
