package com.tguimaraes.ledger.core.adapter.inbound.web.mapper

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.BalanceResponse
import com.tguimaraes.ledger.core.application.dto.AccountBalanceResult

object AccountMapper {

    fun toResponse(
        result: AccountBalanceResult
    ): BalanceResponse =
        BalanceResponse(
            accountId = result.accountId,
            balance = result.balance
        )
}