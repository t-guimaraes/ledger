package com.ledger.core.adapters.output.persistence.mapper

import com.ledger.core.adapters.output.persistence.entity.TransactionJpaEntity
import com.ledger.core.domain.model.Transaction

object TransactionPersistenceMapper {

    fun toEntity(domain: Transaction): TransactionJpaEntity {
        return TransactionJpaEntity(
            id = domain.id,
            amount = domain.amount,
            createdAt = domain.createdAt
        )
    }
}