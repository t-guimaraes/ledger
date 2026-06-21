package com.tguimaraes.ledger.core.application.port.input

import com.tguimaraes.ledger.core.application.dto.account.CreateAccountCommand
import com.tguimaraes.ledger.core.application.dto.account.CreateAccountResult

interface CreateAccountInputPort {
    fun execute(
        command: CreateAccountCommand
    ): CreateAccountResult
}