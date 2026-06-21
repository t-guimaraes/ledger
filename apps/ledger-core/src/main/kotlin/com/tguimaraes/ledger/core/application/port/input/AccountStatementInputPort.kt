package com.tguimaraes.ledger.core.application.port.input

import com.tguimaraes.ledger.core.application.dto.account.AccountStatementResult
import java.util.*

interface AccountStatementInputPort {

    fun execute(
        accountId: UUID
    ): AccountStatementResult
}