package com.tguimaraes.ledger.core.application.port.input

import com.tguimaraes.ledger.core.application.dto.CreateTransferCommand

interface CreateTransferInputPort {

    fun transfer(request: CreateTransferCommand, idempotencyKey: String)
}