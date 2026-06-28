package com.tguimaraes.ledger.core.adapter.outbound.persistence

import com.tguimaraes.ledger.core.adapter.outbound.persistence.mapper.OutboxMapper
import com.tguimaraes.ledger.core.adapter.outbound.persistence.repository.OutboxJpaRepository
import com.tguimaraes.ledger.core.application.port.output.repository.OutboxRepositoryPort
import com.tguimaraes.ledger.core.domain.event.outbox.OutboxEvent
import org.springframework.stereotype.Component

@Component
class OutboxPersistenceAdapter(
    private val repository: OutboxJpaRepository
) : OutboxRepositoryPort {

    override fun save(event: OutboxEvent) {
        repository.save(OutboxMapper.toEntity(event))
    }
}