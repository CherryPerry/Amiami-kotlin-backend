package com.cherryperry.amiami.controller

import com.cherryperry.amiami.model.mongodb.Item
import com.cherryperry.amiami.model.mongodb.ItemRepository
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.WebRequest

@RestController
class DataController @Autowired constructor(
    private val itemRepository: ItemRepository
) {

    private val log = LogManager.getLogger(DataController::class.java)

    @GetMapping(value = ["/v1/data"])
    fun data(request: WebRequest): ResponseEntity<Collection<Item>>? {
        log.trace("data")
        val lastModified = itemRepository.lastModified()
        if (request.checkNotModified(lastModified)) {
            log.info("Not modified")
            return ResponseEntity(HttpStatus.NOT_MODIFIED)
        }
        val items = itemRepository.items()
        return ResponseEntity.ok()
            .lastModified(lastModified)
            .contentType(MediaType.APPLICATION_JSON)
            .body(items)
    }
}