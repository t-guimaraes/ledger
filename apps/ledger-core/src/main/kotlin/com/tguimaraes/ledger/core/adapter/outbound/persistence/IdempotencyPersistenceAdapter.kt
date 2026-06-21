package com.tguimaraes.ledger.core.adapter.outbound.persistence

import com.tguimaraes.ledger.core.adapter.outbound.persistence.entity.IdempotencyKeyJpaEntity
import com.tguimaraes.ledger.core.adapter.outbound.persistence.repository.IdempotencyKeyJpaRepository
import com.tguimaraes.ledger.core.application.port.output.idempotency.IdempotencyPort
import com.tguimaraes.ledger.core.domain.exception.IdempotencyException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class IdempotencyPersistenceAdapter(
    private val repository: IdempotencyKeyJpaRepository
) : IdempotencyPort {

    override fun exists(key: String): Boolean =
        repository.existsById(key)

    override fun save(key: String) {
        try {
            repository.save(
                IdempotencyKeyJpaEntity(
                    key = key,
                    createdAt = Instant.now()
                )
            )
        } catch (ex: DataIntegrityViolationException) {
            throw IdempotencyException()
        }
    }
}
