package com.tguimaraes.ledger.core.domain.event.outbox

import com.fasterxml.jackson.databind.JsonNode
import java.time.Instant
import java.util.*

data class OutboxEvent(
    val id: UUID = UUID.randomUUID(),
    val aggregateId: String,
    val aggregateType: String,
    val eventType: String,
    val topic: String,
    val payload: JsonNode,
    val createdAt: Instant = Instant.now(),
)