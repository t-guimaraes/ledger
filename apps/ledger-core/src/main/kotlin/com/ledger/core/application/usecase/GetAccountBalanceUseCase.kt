package com.ledger.core.application.usecase

import com.ledger.core.application.port.output.EntryQueryPort
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.UUID

@Service
class GetAccountBalanceUseCase(
    private val entryQueryPort: EntryQueryPort
) {

    fun execute(accountId: UUID): BigDecimal {
        return entryQueryPort.getBalance(accountId)
    }
}