package com.tguimaraes.ledger.core.adapters.output.persistence.mapper

import com.tguimaraes.ledger.core.adapters.output.persistence.entity.AccountJpaEntity
import com.tguimaraes.ledger.core.domain.model.Account

object AccountPersistenceMapper {

    fun toDomain(entity: AccountJpaEntity): Account {
        return Account(
            id = entity.id,
            ownerName = entity.ownerName,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            version = entity.version
        )
    }

    fun toEntity(domain: Account): AccountJpaEntity {
        return AccountJpaEntity(
            id = domain.id,
            ownerName = domain.ownerName,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            version = domain.version
        )
    }
}