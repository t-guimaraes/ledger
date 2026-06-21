package com.tguimaraes.ledger.core.adapter.inbound.web.mapper.account

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.account.AccountWithdrawRequest
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.account.AccountWithdrawResponse
import com.tguimaraes.ledger.core.application.dto.account.AccountWithdrawCommand
import com.tguimaraes.ledger.core.application.dto.account.AccountWithdrawResult

object AccountWithdrawMapper {

    fun toCommand(request: AccountWithdrawRequest) =
        AccountWithdrawCommand(
            amount = request.amount
        )

    fun toResponse(result: AccountWithdrawResult) =
        AccountWithdrawResponse(
            accountId = result.accountId,
            amount = result.amount
        )
}
