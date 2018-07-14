package com.cherryperry.amiami.model.lastmodified

import org.apache.logging.log4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.context.request.WebRequest

class LastModifiedControllerHandler(
    private val parentLogger: Logger
) {

    fun <T> handle(
        lastModifiedSupported: LastModifiedSupported,
        request: WebRequest,
        action: (Long) -> ResponseEntity<T>
    ): ResponseEntity<T> {
        val lastModified = lastModifiedSupported.lastModified
        if (request.checkNotModified(lastModified)) {
            parentLogger.info("Not modified")
            return ResponseEntity(HttpStatus.NOT_MODIFIED)
        }
        return action(lastModified)
    }
}
