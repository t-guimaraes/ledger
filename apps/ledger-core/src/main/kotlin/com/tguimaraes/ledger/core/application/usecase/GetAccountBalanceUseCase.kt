package com.tguimaraes.ledger.core.application.usecase

import com.tguimaraes.ledger.core.application.dto.AccountBalanceResult
import com.tguimaraes.ledger.core.application.port.input.GetAccountBalanceInputPort
import com.tguimaraes.ledger.core.application.port.output.query.EntryQueryPort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetAccountBalanceUseCase(
    private val entryQueryPort: EntryQueryPort
): GetAccountBalanceInputPort {

    override fun execute(accountId: UUID): AccountBalanceResult {
        return AccountBalanceResult(
            accountId = accountId,
            balance = entryQueryPort.getBalance(accountId)
        )
    }
}