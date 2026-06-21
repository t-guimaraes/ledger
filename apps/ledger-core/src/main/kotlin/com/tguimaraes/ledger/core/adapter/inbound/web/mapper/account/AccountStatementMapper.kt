package com.tguimaraes.ledger.core.adapter.inbound.web.mapper.account

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.account.AccountStatementEntryResponse
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.account.AccountStatementResponse
import com.tguimaraes.ledger.core.application.dto.account.AccountStatementResult

object AccountStatementMapper {

    fun toResponse(
        result: AccountStatementResult
    ): AccountStatementResponse =
        AccountStatementResponse(
            accountId = result.accountId,
            entries = result.entries.map {
                AccountStatementEntryResponse(
                    transactionId = it.transactionId,
                    type = it.type,
                    amount = it.amount,
                    createdAt = it.createdAt
                )
            }
        )
}