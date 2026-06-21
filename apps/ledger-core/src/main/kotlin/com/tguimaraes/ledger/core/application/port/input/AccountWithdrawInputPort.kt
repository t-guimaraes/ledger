package com.tguimaraes.ledger.core.application.port.input

import com.tguimaraes.ledger.core.application.dto.account.AccountWithdrawCommand
import com.tguimaraes.ledger.core.application.dto.account.AccountWithdrawResult
import java.util.*

interface AccountWithdrawInputPort {
    fun withdraw(
        command: AccountWithdrawCommand,
        accountId: UUID,
        idempotencyKey: String
    ): AccountWithdrawResult
}