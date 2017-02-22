package com.cherryperry.amiami.controller

import com.cherryperry.amiami.model.update.Update
import com.cherryperry.amiami.model.update.UpdateInfo
import org.apache.logging.log4j.LogManager
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UpdateController {

    private val log = LogManager.getLogger(UpdateController::class.java)!!

    @RequestMapping(value = "/v1/start_update", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun startUpdate(): UpdateInfo {
        log.trace("startUpdate")
        Update.sync()
        return UpdateInfo()
    }
}
