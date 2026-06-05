package com.ledger.core.adapters.output.persistence

import com.ledger.core.application.port.output.EntryQueryPort
import com.ledger.core.adapters.output.persistence.repository.EntryJpaRepository
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.UUID

@Component
class EntryQueryPersistenceAdapter(
    private val entryJpaRepository: EntryJpaRepository
) : EntryQueryPort {

    override fun getBalance(
        accountId: UUID
    ): BigDecimal {

        return entryJpaRepository.getBalance(accountId)
    }
}