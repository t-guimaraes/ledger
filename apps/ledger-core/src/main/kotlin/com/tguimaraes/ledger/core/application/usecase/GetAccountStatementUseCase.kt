package com.tguimaraes.ledger.core.application.usecase

import com.tguimaraes.ledger.core.application.dto.AccountStatementResult
import com.tguimaraes.ledger.core.application.port.input.GetAccountStatementInputPort
import com.tguimaraes.ledger.core.application.port.output.query.EntryQueryPort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetAccountStatementUseCase(
    private val entryQueryPort: EntryQueryPort
) : GetAccountStatementInputPort {

    override fun execute(
        accountId: UUID
    ): AccountStatementResult {

        return AccountStatementResult(
            accountId = accountId,
            entries = entryQueryPort.getStatement(accountId)
        )
    }
}