package com.tguimaraes.ledger.core.adapter.outbound.persistence.mapper

import com.tguimaraes.ledger.core.adapter.outbound.persistence.entity.EntryJpaEntity
import com.tguimaraes.ledger.core.domain.model.Entry

object EntryPersistenceMapper {

    fun toDomain(entity: EntryJpaEntity): Entry {
        return Entry(
            id = entity.id,
            transactionId = entity.transactionId,
            accountId = entity.accountId,
            type = entity.type,
            amount = entity.amount,
            createdAt = entity.createdAt
        )
    }

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