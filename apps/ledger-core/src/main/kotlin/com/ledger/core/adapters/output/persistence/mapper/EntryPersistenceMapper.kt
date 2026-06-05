package com.ledger.core.adapters.output.persistence.mapper

import com.ledger.core.adapters.output.persistence.entity.EntryJpaEntity
import com.ledger.core.domain.model.Entry

object EntryPersistenceMapper {

    fun toEntity(domain: Entry): EntryJpaEntity {
        return EntryJpaEntity(
            id = domain.id,
            transactionId = domain.transactionId,
            accountId = domain.accountId,
            type = domain.type,
            amount = domain.amount,
            createdAt = domain.createdAt
        )
    }
}