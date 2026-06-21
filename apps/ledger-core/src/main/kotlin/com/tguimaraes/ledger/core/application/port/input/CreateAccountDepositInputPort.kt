package com.tguimaraes.ledger.core.application.port.input

import com.tguimaraes.ledger.core.application.dto.CreateAccountDepositCommand
import com.tguimaraes.ledger.core.application.dto.CreateAccountDepositResult
import java.util.UUID

interface CreateAccountDepositInputPort {
    fun deposit(
        command: CreateAccountDepositCommand,
        accountId: UUID,
        idempotencyKey: String
    ): CreateAccountDepositResult
}