package com.cherryperry.amiami.controller

import com.cherryperry.amiami.model.currency.CurrencyRepository
import com.cherryperry.amiami.model.currency.CurrencyResponse
import com.cherryperry.amiami.model.lastmodified.LastModifiedControllerHandler
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.WebRequest

@RestController
class CurrencyController @Autowired constructor(
    private val currencyRepository: CurrencyRepository
) {

    private val log = LogManager.getLogger(CurrencyController::class.java)
    private val handler = LastModifiedControllerHandler(log)

    @RequestMapping(value = ["/v1/currency"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun currencies(request: WebRequest): ResponseEntity<CurrencyResponse> {
        log.trace("currencies")
        return handler.handle(currencyRepository, request) { lastModified ->
            val response = currencyRepository.get()
            ResponseEntity.ok()
                .lastModified(lastModified)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response)
        }
    }
}