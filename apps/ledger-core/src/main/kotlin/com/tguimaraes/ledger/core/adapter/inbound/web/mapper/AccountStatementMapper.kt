package com.tguimaraes.ledger.core.adapter.inbound.web.mapper

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.AccountStatementResponse
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.AccountStatementEntryResponse
import com.tguimaraes.ledger.core.application.dto.AccountStatementResult

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