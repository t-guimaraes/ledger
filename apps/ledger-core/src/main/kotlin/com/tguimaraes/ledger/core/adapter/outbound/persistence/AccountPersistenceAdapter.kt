package com.tguimaraes.ledger.core.adapter.outbound.persistence

import com.tguimaraes.ledger.core.adapter.outbound.persistence.mapper.AccountPersistenceMapper
import com.tguimaraes.ledger.core.adapter.outbound.persistence.repository.AccountJpaRepository
import com.tguimaraes.ledger.core.application.port.output.repository.AccountRepositoryPort
import com.tguimaraes.ledger.core.domain.model.Account
import org.springframework.stereotype.Component
import java.util.*

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