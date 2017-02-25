package com.cherryperry.amiami.component

import com.cherryperry.amiami.model.update.Update
import org.apache.logging.log4j.LogManager
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Suppress("unused")
@Component
class ScheduledUpdateComponent {

    private val log = LogManager.getLogger(ScheduledUpdateComponent::class.java)!!

    @Scheduled(cron = "0 0 * * * *")
    fun update() {
        log.trace("update")
        Update.sync()
    }
}