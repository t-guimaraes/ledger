package com.tguimaraes.ledger.core.application.usecase

import com.tguimaraes.ledger.core.application.dto.account.AccountBalanceResult
import com.tguimaraes.ledger.core.application.port.input.AccountBalanceInputPort
import com.tguimaraes.ledger.core.application.port.output.query.EntryQueryPort
import com.tguimaraes.ledger.core.application.port.output.repository.AccountRepositoryPort
import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import java.util.*

class AccountBalanceUseCase(
    private val entryQueryPort: EntryQueryPort,
    private val accountRepositoryPort: AccountRepositoryPort
): AccountBalanceInputPort {

    override fun execute(accountId: UUID): AccountBalanceResult {

        accountRepositoryPort.findById(accountId) ?: throw AccountNotFoundException(accountId)

        return AccountBalanceResult(
            accountId = accountId,
            balance = entryQueryPort.getBalance(accountId)
        )
    }
}