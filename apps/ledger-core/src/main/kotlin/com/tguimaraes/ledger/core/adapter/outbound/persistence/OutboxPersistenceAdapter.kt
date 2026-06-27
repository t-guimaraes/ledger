package com.tguimaraes.ledger.core.adapter.outbound.persistence

import com.tguimaraes.ledger.core.adapter.outbound.persistence.mapper.OutboxMapper
import com.tguimaraes.ledger.core.adapter.outbound.persistence.repository.OutboxJpaRepository
import com.tguimaraes.ledger.core.application.port.output.repository.OutboxRepositoryPort
import com.tguimaraes.ledger.core.domain.event.outbox.OutboxEvent
import com.tguimaraes.ledger.core.domain.model.OutboxStatus
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
class OutboxPersistenceAdapter(
    private val repository: OutboxJpaRepository
) : OutboxRepositoryPort {

    override fun save(event: OutboxEvent) {
        repository.save(OutboxMapper.toEntity(event))
    }

    override fun findByStatus(status: OutboxStatus): List<OutboxEvent> {
        return repository.findByStatus(status).map {
            OutboxMapper.toDomain(it)
        }
    }

    override fun markPublished(id: String) {
        val event = repository.findById(UUID.fromString(id)).orElseThrow()

        repository.save(
            event.copy(
                status = OutboxStatus.PUBLISHED,
                publishedAt = Instant.now()
            )
        )
    }

    override fun markFailed(id: String) {
        val event = repository.findById(UUID.fromString(id)).orElseThrow()

        repository.save(
            event.copy(
                status = OutboxStatus.FAILED,
                retryCount = event.retryCount + 1
            )
        )
    }
}