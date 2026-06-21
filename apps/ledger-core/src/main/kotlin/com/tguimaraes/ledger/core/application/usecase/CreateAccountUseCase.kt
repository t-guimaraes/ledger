package com.tguimaraes.ledger.core.application.usecase

import com.tguimaraes.ledger.core.application.dto.CreateAccountCommand
import com.tguimaraes.ledger.core.application.dto.CreateAccountResult
import com.tguimaraes.ledger.core.application.port.input.CreateAccountInputPort
import com.tguimaraes.ledger.core.application.port.output.id.IdGeneratorPort
import com.tguimaraes.ledger.core.application.port.output.repository.AccountRepositoryPort
import com.tguimaraes.ledger.core.domain.model.Account
import java.time.Clock
import java.time.Instant

class CreateAccountUseCase(
    private val accountRepository: AccountRepositoryPort,
    private val idGenerator: IdGeneratorPort,
) : CreateAccountInputPort {

    override fun execute(
        command: CreateAccountCommand
    ): CreateAccountResult {

        val ownerName = Account.normalizeOwnerName(command.ownerName)

        val account = Account.create(
            id = idGenerator.generate(),
            ownerName = ownerName,
            createdAt = Instant.now()
        )

        accountRepository.save(account)

        return CreateAccountResult(
            accountId = account.id
        )
    }
}
