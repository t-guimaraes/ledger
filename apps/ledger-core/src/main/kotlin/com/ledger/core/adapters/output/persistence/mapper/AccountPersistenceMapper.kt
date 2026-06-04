package com.ledger.core.adapters.output.persistence.mapper

import com.ledger.core.adapters.output.persistence.entity.AccountJpaEntity
import com.ledger.core.domain.model.Account

object AccountPersistenceMapper {

    fun toDomain(entity: AccountJpaEntity): Account {
        return Account(
            id = entity.id,
            ownerName = entity.ownerName,
            createdAt = entity.createdAt,
            version = entity.version
        )
    }

    fun toEntity(domain: Account): AccountJpaEntity {
        return AccountJpaEntity(
            id = domain.id,
            ownerName = domain.ownerName,
            createdAt = domain.createdAt,
            version = domain.version
        )
    }
}