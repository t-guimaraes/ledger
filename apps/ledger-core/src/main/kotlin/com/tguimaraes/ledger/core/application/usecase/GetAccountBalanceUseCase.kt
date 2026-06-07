package com.tguimaraes.ledger.core.application.usecase

import com.tguimaraes.ledger.core.application.port.input.GetAccountBalanceInputPort
import com.tguimaraes.ledger.core.application.port.output.EntryQueryPort
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.UUID

@Service
class GetAccountBalanceUseCase(
    private val entryQueryPort: EntryQueryPort
): GetAccountBalanceInputPort {

    override fun execute(accountId: UUID): BigDecimal {
        return entryQueryPort.getBalance(accountId)
    }
}