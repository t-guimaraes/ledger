package com.ledger.core.application.port.input

import com.ledger.core.application.dto.CreateTransferRequest

interface CreateTransferInputPort {

    fun transfer(request: CreateTransferRequest, idempotencyKey: String)
}