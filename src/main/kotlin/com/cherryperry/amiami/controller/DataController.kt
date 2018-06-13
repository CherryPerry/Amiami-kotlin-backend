package com.cherryperry.amiami.controller

import com.cherryperry.amiami.model.lastmodified.LastModifiedControllerHandler
import com.cherryperry.amiami.model.mongodb.Item
import com.cherryperry.amiami.model.mongodb.ItemRepository
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.CacheControl
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.WebRequest
import java.util.concurrent.TimeUnit

@RestController
class DataController @Autowired constructor(
    private val itemRepository: ItemRepository
) {

    private val log = LogManager.getLogger(DataController::class.java)
    private val handler = LastModifiedControllerHandler(log)

    @GetMapping(value = ["/v1/data"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun data(request: WebRequest): ResponseEntity<Collection<Item>>? {
        log.trace("data")
        return handler.handle(itemRepository, request) { lastModified ->
            val items = itemRepository.items()
            ResponseEntity.ok()
                .lastModified(lastModified)
                .contentType(MediaType.APPLICATION_JSON)
                .cacheControl(CacheControl
                    .maxAge(0, TimeUnit.SECONDS)
                    .staleIfError(7, TimeUnit.DAYS)
                    .cachePrivate())
                .body(items)
        }
    }
}