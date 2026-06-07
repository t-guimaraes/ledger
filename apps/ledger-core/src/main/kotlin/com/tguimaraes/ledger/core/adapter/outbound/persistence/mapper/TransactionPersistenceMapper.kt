package com.tguimaraes.ledger.core.adapter.outbound.persistence.mapper

import com.tguimaraes.ledger.core.adapter.outbound.persistence.entity.TransactionJpaEntity
import com.tguimaraes.ledger.core.domain.model.Transaction

object TransactionPersistenceMapper {

    fun toEntity(domain: Transaction): TransactionJpaEntity {
        return TransactionJpaEntity(
            id = domain.id,
            amount = domain.amount,
            createdAt = domain.createdAt
        )
    }
}