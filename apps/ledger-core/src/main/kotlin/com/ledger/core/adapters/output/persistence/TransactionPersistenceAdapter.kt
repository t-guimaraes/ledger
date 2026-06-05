package com.ledger.core.adapters.output.persistence

import com.ledger.core.adapters.output.persistence.mapper.TransactionPersistenceMapper
import com.ledger.core.adapters.output.persistence.repository.TransactionJpaRepository
import com.ledger.core.application.port.output.TransactionRepositoryPort
import com.ledger.core.domain.model.Transaction
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