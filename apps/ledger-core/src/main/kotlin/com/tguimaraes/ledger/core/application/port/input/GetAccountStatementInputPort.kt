package com.tguimaraes.ledger.core.application.port.input

import com.tguimaraes.ledger.core.application.dto.AccountStatementResult
import java.util.UUID

interface GetAccountStatementInputPort {

    fun execute(
        accountId: UUID
    ): AccountStatementResult
}