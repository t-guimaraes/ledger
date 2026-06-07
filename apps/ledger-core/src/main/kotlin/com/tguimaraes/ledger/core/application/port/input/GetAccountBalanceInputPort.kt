package com.tguimaraes.ledger.core.application.port.input

import com.tguimaraes.ledger.core.application.dto.AccountBalanceResult
import java.util.UUID

interface GetAccountBalanceInputPort {
    fun execute(accountId: UUID): AccountBalanceResult
}