package com.cherryperry.amiami.controller

import com.cherryperry.amiami.model.mongodb.Item
import com.cherryperry.amiami.model.mongodb.ItemRepository
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DataController @Autowired constructor(
    private val itemRepository: ItemRepository
) {

    private val log = LogManager.getLogger(DataController::class.java)

    @RequestMapping(value = ["/v1/data"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun data(): Collection<Item> {
        log.trace("data")
        return itemRepository.items()
    }
}