package com.tguimaraes.ledger.core.adapter.inbound.web.mapper

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.AccountBalanceResponse
import com.tguimaraes.ledger.core.application.dto.AccountBalanceResult

object AccountBalanceMapper {

    fun toResponse(
        result: AccountBalanceResult
    ): AccountBalanceResponse =
        AccountBalanceResponse(
            accountId = result.accountId,
            balance = result.balance
        )
}