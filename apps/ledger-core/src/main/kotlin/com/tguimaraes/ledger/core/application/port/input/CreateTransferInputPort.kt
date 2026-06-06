package com.tguimaraes.ledger.core.application.port.input

import com.tguimaraes.ledger.core.application.dto.CreateTransferRequest

interface CreateTransferInputPort {

    fun transfer(request: CreateTransferRequest, idempotencyKey: String)
}