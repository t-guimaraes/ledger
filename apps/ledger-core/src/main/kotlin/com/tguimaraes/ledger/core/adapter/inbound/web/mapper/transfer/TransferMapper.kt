package com.tguimaraes.ledger.core.adapter.inbound.web.mapper.transfer

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.transfer.TransferRequest
import com.tguimaraes.ledger.core.application.dto.transfer.TransferCommand

object TransferMapper {

    fun toCommand(
        request: TransferRequest
    ) =
        TransferCommand(
            fromAccountId = request.fromAccountId,
            toAccountId = request.toAccountId,
            amount = request.amount
        )
}