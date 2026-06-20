package com.tguimaraes.ledger.core.application.usecase

import com.tguimaraes.ledger.core.application.dto.AccountBalanceResult
import com.tguimaraes.ledger.core.application.port.input.GetAccountBalanceInputPort
import com.tguimaraes.ledger.core.application.port.output.query.EntryQueryPort
import com.tguimaraes.ledger.core.application.port.output.repository.AccountRepositoryPort
import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import java.util.UUID

class GetAccountBalanceUseCase(
    private val entryQueryPort: EntryQueryPort,
    private val accountRepositoryPort: AccountRepositoryPort
): GetAccountBalanceInputPort {

    override fun execute(accountId: UUID): AccountBalanceResult {

        accountRepositoryPort.findById(accountId) ?: throw AccountNotFoundException(accountId)

        return AccountBalanceResult(
            accountId = accountId,
            balance = entryQueryPort.getBalance(accountId)
        )
    }
}