package com.cherryperry.amiami.controller

import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UpdateController @Autowired constructor(
    private val updateComponent: com.cherryperry.amiami.model.update.UpdateComponent
) {

    private val log = LogManager.getLogger(UpdateController::class.java)

    @RequestMapping(value = ["/v1/u"])
    fun forceUpdate(): ResponseEntity<HttpStatus> {
        log.trace("forceUpdate")
        updateComponent.sync()
        return ResponseEntity.ok().build()
    }
}