package com.tguimaraes.ledger.core.adapter.outbound.persistence

import com.tguimaraes.ledger.core.application.port.output.query.EntryQueryPort
import com.tguimaraes.ledger.core.adapter.outbound.persistence.repository.EntryJpaRepository
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