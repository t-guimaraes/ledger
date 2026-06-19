package com.tguimaraes.ledger.core.adapter.inbound.web.doc

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.AccountStatementResponse
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.AccountBalanceResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import java.util.UUID

@Tag(name = "Accounts")
interface AccountApi {

    @Operation(
        summary = "Get account balance",
        description = "Returns current account balance"
    )
    fun getBalance(
        accountId: UUID,
    ): AccountBalanceResponse

    @Operation(
        summary = "Get account statement",
        description = "Returns current account statement"
    )
    fun getStatement(
        accountId: UUID,
    ): AccountStatementResponse
}