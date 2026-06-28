package com.tguimaraes.ledger.core.adapter.outbound.persistence.mapper

import com.tguimaraes.ledger.core.adapter.outbound.persistence.entity.OutboxJpaEntity
import com.tguimaraes.ledger.core.domain.event.outbox.OutboxEvent

object OutboxMapper {

    fun toEntity(domain: OutboxEvent) =
        OutboxJpaEntity(
            id = domain.id,
            aggregateId = domain.aggregateId,
            aggregateType = domain.aggregateType,
            eventType = domain.eventType,
            payload = domain.payload,
            topic = domain.topic,
            createdAt = domain.createdAt
        )

    fun toDomain(entity: OutboxJpaEntity) =
        OutboxEvent(
            id = entity.id,
            aggregateId = entity.aggregateId,
            aggregateType = entity.aggregateType,
            eventType = entity.eventType,
            payload = entity.payload,
            topic = entity.topic,
            createdAt = entity.createdAt,
        )
}