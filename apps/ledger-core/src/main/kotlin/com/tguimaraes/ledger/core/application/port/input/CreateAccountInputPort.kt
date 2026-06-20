package com.tguimaraes.ledger.core.application.port.input

import com.tguimaraes.ledger.core.application.dto.CreateAccountCommand
import com.tguimaraes.ledger.core.application.dto.CreateAccountResult

interface CreateAccountInputPort {
    fun execute(
        command: CreateAccountCommand
    ): CreateAccountResult
}