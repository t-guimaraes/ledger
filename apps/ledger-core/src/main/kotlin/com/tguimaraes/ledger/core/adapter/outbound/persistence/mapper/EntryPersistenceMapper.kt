package com.tguimaraes.ledger.core.adapter.outbound.persistence.mapper

import com.tguimaraes.ledger.core.adapter.outbound.persistence.entity.EntryJpaEntity
import com.tguimaraes.ledger.core.domain.model.Entry

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