package com.tguimaraes.ledger.core.application.port.output.event

import com.tguimaraes.ledger.core.domain.event.outbox.OutboxEvent

interface EventPublisherPort {
    fun publish(event: OutboxEvent)
}