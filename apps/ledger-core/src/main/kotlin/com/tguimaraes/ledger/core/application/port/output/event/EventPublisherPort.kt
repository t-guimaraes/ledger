package com.tguimaraes.ledger.core.application.port.output.event

import com.tguimaraes.ledger.core.domain.event.DomainEvent

interface EventPublisherPort {
    fun publish(event: DomainEvent)
}