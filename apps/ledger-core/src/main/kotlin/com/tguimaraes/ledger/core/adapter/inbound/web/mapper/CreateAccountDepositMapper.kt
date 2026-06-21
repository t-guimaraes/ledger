package com.tguimaraes.ledger.core.adapter.inbound.web.mapper

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.AccountDepositRequest
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.AccountDepositResponse
import com.tguimaraes.ledger.core.application.dto.CreateAccountDepositCommand
import com.tguimaraes.ledger.core.application.dto.CreateAccountDepositResult

object CreateAccountDepositMapper {

    fun toCommand(request: AccountDepositRequest) =
        CreateAccountDepositCommand(
            amount = request.amount
        )

    fun toResponse(result: CreateAccountDepositResult) =
        AccountDepositResponse(
            accountId = result.accountId,
            amount = result.amount
        )
}
