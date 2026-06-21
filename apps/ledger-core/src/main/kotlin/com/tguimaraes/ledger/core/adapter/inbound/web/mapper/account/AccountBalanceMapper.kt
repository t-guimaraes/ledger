package com.tguimaraes.ledger.core.adapter.inbound.web.mapper.account

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.account.AccountBalanceResponse
import com.tguimaraes.ledger.core.application.dto.account.AccountBalanceResult

object AccountBalanceMapper {

    fun toResponse(
        result: AccountBalanceResult
    ): AccountBalanceResponse =
        AccountBalanceResponse(
            accountId = result.accountId,
            balance = result.balance
        )
}