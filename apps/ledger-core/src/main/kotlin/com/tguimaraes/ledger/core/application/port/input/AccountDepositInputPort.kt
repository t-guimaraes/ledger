package com.tguimaraes.ledger.core.application.port.input

import com.tguimaraes.ledger.core.application.dto.account.AccountDepositCommand
import com.tguimaraes.ledger.core.application.dto.account.AccountDepositResult
import java.util.*

interface AccountDepositInputPort {
    fun deposit(
        command: AccountDepositCommand,
        accountId: UUID,
        idempotencyKey: String
    ): AccountDepositResult
}