package com.tguimaraes.ledger.core.adapter.inbound.web.mapper

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.CreateTransferRequest
import com.tguimaraes.ledger.core.application.dto.CreateTransferCommand

object TransferWebMapper {

    fun toCommand(
        request: CreateTransferRequest
    ) =
        CreateTransferCommand(
            fromAccountId = request.fromAccountId,
            toAccountId = request.toAccountId,
            amount = request.amount
        )
}