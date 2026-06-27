package com.tguimaraes.ledger.core.adapter.outbound.event.outbox

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class OutboxScheduler(
    private val publisher: OutboxPublisher
) {

    @Scheduled(fixedDelay = 5000)
    fun run() {
        publisher.publishPendingEvents()
    }
}