package com.tguimaraes.ledger.core.application.port.input

import com.tguimaraes.ledger.core.application.dto.account.AccountBalanceResult
import java.util.*

interface AccountBalanceInputPort {
    fun execute(accountId: UUID): AccountBalanceResult
}