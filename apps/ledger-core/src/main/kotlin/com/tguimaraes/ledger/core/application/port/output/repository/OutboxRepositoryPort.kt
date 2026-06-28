package com.tguimaraes.ledger.core.application.port.output.repository

import com.tguimaraes.ledger.core.domain.event.outbox.OutboxEvent

interface OutboxRepositoryPort {

    fun save(event: OutboxEvent)
}