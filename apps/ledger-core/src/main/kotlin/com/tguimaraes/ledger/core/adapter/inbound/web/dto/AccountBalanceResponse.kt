package com.tguimaraes.ledger.core.adapter.inbound.web.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.util.UUID

data class AccountBalanceResponse(
    @field:Schema(
        example = "11111111-1111-1111-1111-111111111111"
    )
    val accountId: UUID,
    @field:Schema(
        example = "100.00"
    )
    val balance: BigDecimal
)