package com.tguimaraes.ledger.core.application.usecase

import com.tguimaraes.ledger.core.application.dto.account.AccountStatementResult
import com.tguimaraes.ledger.core.application.port.input.AccountStatementInputPort
import com.tguimaraes.ledger.core.application.port.output.query.EntryQueryPort
import com.tguimaraes.ledger.core.application.port.output.repository.AccountRepositoryPort
import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import java.util.*

class AccountStatementUseCase(
    private val entryQueryPort: EntryQueryPort,
    private val accountRepositoryPort: AccountRepositoryPort
) : AccountStatementInputPort {

    override fun execute(
        accountId: UUID
    ): AccountStatementResult {

        accountRepositoryPort.findById(accountId) ?: throw AccountNotFoundException(accountId)

        return AccountStatementResult(
            accountId = accountId,
            entries = entryQueryPort.getStatement(accountId)
        )
    }
}