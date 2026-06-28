package com.tguimaraes.ledger.core.adapter.outbound.persistence.entity

import com.fasterxml.jackson.databind.JsonNode
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.*

@Entity
@Table(name = "outbox")
data class OutboxJpaEntity(
    @Id
    val id: UUID,
    val aggregateId: String,
    val aggregateType: String,
    val eventType: String,
    val topic: String,

    @JdbcTypeCode(SqlTypes.JSON)
    val payload: JsonNode,

    val createdAt: Instant
)
