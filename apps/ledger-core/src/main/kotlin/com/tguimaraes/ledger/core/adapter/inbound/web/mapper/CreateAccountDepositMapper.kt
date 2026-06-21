package com.tguimaraes.ledger.core.adapter.inbound.web.mapper

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.CreateAccountDepositRequest
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.CreateAccountDepositResponse
import com.tguimaraes.ledger.core.application.dto.CreateAccountDepositCommand
import com.tguimaraes.ledger.core.application.dto.CreateAccountDepositResult

object CreateAccountDepositMapper {

    fun toCommand(request: CreateAccountDepositRequest) =
        CreateAccountDepositCommand(
            amount = request.amount
        )

    fun toResponse(result: CreateAccountDepositResult) =
        CreateAccountDepositResponse(
            accountId = result.accountId,
            amount = result.amount
        )
}
