package com.ledger.core.adapters.output.persistence

import com.ledger.core.adapters.output.persistence.mapper.AccountPersistenceMapper
import com.ledger.core.adapters.output.persistence.repository.AccountJpaRepository
import com.ledger.core.application.port.output.AccountRepositoryPort
import com.ledger.core.domain.model.Account
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class AccountPersistenceAdapter(
    private val accountJpaRepository: AccountJpaRepository
) : AccountRepositoryPort {

    override fun findById(id: UUID): Account? {
        return accountJpaRepository.findById(id)
            .map(AccountPersistenceMapper::toDomain)
            .orElse(null)
    }

    override fun save(account: Account) {
        val entity = AccountPersistenceMapper.toEntity(account)

        accountJpaRepository.save(entity)
    }
}