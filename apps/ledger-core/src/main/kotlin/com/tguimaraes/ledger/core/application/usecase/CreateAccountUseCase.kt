package com.tguimaraes.ledger.core.application.usecase

import com.tguimaraes.ledger.core.application.dto.account.CreateAccountCommand
import com.tguimaraes.ledger.core.application.dto.account.CreateAccountResult
import com.tguimaraes.ledger.core.application.port.input.CreateAccountInputPort
import com.tguimaraes.ledger.core.application.port.output.repository.AccountRepositoryPort
import com.tguimaraes.ledger.core.domain.model.Account
import java.time.Instant
import java.util.*

class CreateAccountUseCase(
    private val accountRepository: AccountRepositoryPort
) : CreateAccountInputPort {

    override fun execute(command: CreateAccountCommand): CreateAccountResult {

        val ownerName = Account.normalizeOwnerName(command.ownerName)
        val account = Account.create(
            id = UUID.randomUUID(),
            ownerName = ownerName,
            createdAt = Instant.now()
        )

        accountRepository.save(account)

        return CreateAccountResult(
            accountId = account.id
        )
    }
}
