package com.tguimaraes.ledger.core.application.port.input

import com.tguimaraes.ledger.core.application.dto.transfer.TransferCommand

interface TransferInputPort {

    fun transfer(
        command: TransferCommand,
        idempotencyKey: String
    )
}