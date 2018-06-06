package com.cherryperry.amiami.controller

import com.cherryperry.amiami.model.push.PushService
import com.cherryperry.amiami.model.update.UpdateComponent
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class DebugController @Autowired constructor(
    private val updateComponent: UpdateComponent,
    private val pushService: PushService
) {

    private val log = LogManager.getLogger(DebugController::class.java)

    @RequestMapping(value = ["/debug/update"])
    fun debugUpdate(): ResponseEntity<HttpStatus> {
        log.trace("debugUpdate")
        updateComponent.sync()
        return ResponseEntity.ok().build()
    }

    @RequestMapping(value = ["/debug/push"])
    fun debugPush(@RequestParam("token") token: String): ResponseEntity<HttpStatus> {
        log.trace("debugPush")
        pushService.sendPushWithUpdateCountToDevice(1, token)
        return ResponseEntity.ok().build()
    }
}