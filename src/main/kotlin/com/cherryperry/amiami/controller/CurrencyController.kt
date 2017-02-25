package com.cherryperry.amiami.controller

import com.cherryperry.amiami.model.currency.CurrencyCache
import org.apache.logging.log4j.LogManager
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Suppress("unused")
@RestController
class CurrencyController {

    private val log = LogManager.getLogger(CurrencyController::class.java)!!

    @RequestMapping(value = "/v1/currency", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun currencies(): String {
        log.trace("currencies")
        return CurrencyCache.get()
    }
}