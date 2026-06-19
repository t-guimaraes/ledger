package com.tguimaraes.ledger.core.adapter.outbound.persistence

import com.tguimaraes.ledger.core.adapter.outbound.persistence.mapper.TransactionPersistenceMapper
import com.tguimaraes.ledger.core.adapter.outbound.persistence.repository.TransactionJpaRepository
import com.tguimaraes.ledger.core.application.port.output.repository.TransactionRepositoryPort
import com.tguimaraes.ledger.core.domain.model.Transaction
import org.springframework.stereotype.Component

@Component
class TransactionPersistenceAdapter(
    private val repository: TransactionJpaRepository
) : TransactionRepositoryPort {

    override fun save(transaction: Transaction) {
        repository.save(
            TransactionPersistenceMapper.toEntity(transaction)
        )
    }
}