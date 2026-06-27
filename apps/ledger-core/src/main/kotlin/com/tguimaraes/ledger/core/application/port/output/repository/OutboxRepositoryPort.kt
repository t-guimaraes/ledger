package com.tguimaraes.ledger.core.application.port.output.repository

import com.tguimaraes.ledger.core.domain.event.outbox.OutboxEvent
import com.tguimaraes.ledger.core.domain.model.OutboxStatus

interface OutboxRepositoryPort {

    fun save(event: OutboxEvent)

    fun findByStatus(status: OutboxStatus): List<OutboxEvent>

    fun markPublished(id: String)

    fun markFailed(id: String)
}