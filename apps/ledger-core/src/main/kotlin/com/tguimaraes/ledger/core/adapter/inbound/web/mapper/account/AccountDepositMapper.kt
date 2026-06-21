package com.tguimaraes.ledger.core.adapter.inbound.web.mapper.account

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.account.AccountDepositRequest
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.account.AccountDepositResponse
import com.tguimaraes.ledger.core.application.dto.account.AccountDepositCommand
import com.tguimaraes.ledger.core.application.dto.account.AccountDepositResult

object AccountDepositMapper {

    fun toCommand(request: AccountDepositRequest) =
        AccountDepositCommand(
            amount = request.amount
        )

    fun toResponse(result: AccountDepositResult) =
        AccountDepositResponse(
            accountId = result.accountId,
            amount = result.amount
        )
}
