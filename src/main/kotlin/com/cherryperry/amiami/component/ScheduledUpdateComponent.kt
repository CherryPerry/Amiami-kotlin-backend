package com.cherryperry.amiami.component

import com.cherryperry.amiami.model.update.UpdateComponent
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ScheduledUpdateComponent @Autowired constructor(
    private val updateComponent: UpdateComponent
) {

    private val log = LogManager.getLogger(ScheduledUpdateComponent::class.java)

    @Scheduled(cron = "0 0 * * * *")
    fun update() {
        log.trace("update")
        updateComponent.sync()
    }
}