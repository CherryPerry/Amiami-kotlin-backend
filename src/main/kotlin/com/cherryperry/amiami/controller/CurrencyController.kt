package com.cherryperry.amiami.controller

import com.cherryperry.amiami.model.currency.CurrencyRepository
import com.cherryperry.amiami.model.currency.CurrencyResponse
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CurrencyController @Autowired constructor(
        private val currencyRepository: CurrencyRepository
) {

    private val log = LogManager.getLogger(CurrencyController::class.java)!!

    @RequestMapping(value = ["/v1/currency"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun currencies(): CurrencyResponse {
        log.trace("currencies")
        return currencyRepository.get()
    }
}