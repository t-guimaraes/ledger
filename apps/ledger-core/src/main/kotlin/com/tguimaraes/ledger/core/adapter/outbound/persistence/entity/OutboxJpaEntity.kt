package com.tguimaraes.ledger.core.adapter.outbound.persistence.entity

import com.fasterxml.jackson.databind.JsonNode
import com.tguimaraes.ledger.core.domain.model.OutboxStatus
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.*

@Entity
@Table(name = "outbox_event")
data class OutboxJpaEntity(
    @Id
    val id: UUID,

    val aggregateId: UUID,
    val aggregateType: String,
    val eventType: String,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    val payload: JsonNode,

    @Enumerated(EnumType.STRING)
    val status: OutboxStatus,

    val retryCount: Int,
    val createdAt: Instant,
    val publishedAt: Instant?
)
