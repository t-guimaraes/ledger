package com.tguimaraes.ledger.core.application.usecase

import com.tguimaraes.ledger.core.application.dto.account.AccountStatementResult
import com.tguimaraes.ledger.core.application.port.input.AccountStatementInputPort
import com.tguimaraes.ledger.core.application.port.output.query.EntryQueryPort
import java.util.*

class AccountStatementUseCase(
    private val entryQueryPort: EntryQueryPort
) : AccountStatementInputPort {

    override fun execute(
        accountId: UUID
    ): AccountStatementResult {

        return AccountStatementResult(
            accountId = accountId,
            entries = entryQueryPort.getStatement(accountId)
        )
    }
}