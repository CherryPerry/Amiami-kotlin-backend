package com.cherryperry.amiami.controller

import com.cherryperry.amiami.model.mongodb.Item
import com.cherryperry.amiami.model.mongodb.Store
import org.apache.logging.log4j.LogManager
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Suppress("unused")
@RestController
class DataController {

    private val log = LogManager.getLogger(DataController::class.java)!!

    @RequestMapping(value = "/v1/data", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun data(): Collection<Item> {
        log.trace("data")
        return Store.items()
    }
}