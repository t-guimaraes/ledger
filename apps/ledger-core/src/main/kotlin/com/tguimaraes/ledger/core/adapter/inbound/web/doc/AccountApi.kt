package com.tguimaraes.ledger.core.adapter.inbound.web.doc

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.BalanceResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
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
    ): BalanceResponse
}