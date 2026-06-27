package com.tguimaraes.ledger.core.domain.event.outbox

import com.fasterxml.jackson.databind.JsonNode
import com.tguimaraes.ledger.core.domain.model.OutboxStatus
import java.time.Instant
import java.util.*

data class OutboxEvent(
    val id: UUID = UUID.randomUUID(),
    val aggregateId: UUID,
    val aggregateType: String,
    val eventType: String,
    val payload: JsonNode,
    var status: OutboxStatus = OutboxStatus.PENDING,
    var retryCount: Int = 0,
    val createdAt: Instant = Instant.now(),
    var publishedAt: Instant? = null
)