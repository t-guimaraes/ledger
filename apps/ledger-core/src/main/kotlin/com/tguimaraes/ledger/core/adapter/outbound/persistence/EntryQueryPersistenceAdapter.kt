package com.tguimaraes.ledger.core.adapter.outbound.persistence

import com.tguimaraes.ledger.core.application.port.output.query.EntryQueryPort
import com.tguimaraes.ledger.core.adapter.outbound.persistence.repository.EntryJpaRepository
import com.tguimaraes.ledger.core.application.dto.StatementEntryResult
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

    override fun getStatement(accountId: UUID
    ): List<StatementEntryResult> {

        return entryJpaRepository
            .findAllByAccountIdOrderByCreatedAtDesc(accountId)
            .map {
                StatementEntryResult(
                    transactionId = it.transactionId,
                    type = it.type,
                    amount = it.amount,
                    createdAt = it.createdAt
                )
            }
    }
}