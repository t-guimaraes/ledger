package com.tguimaraes.ledger.core.adapter.inbound.web.dto

import java.math.BigDecimal
import java.util.UUID

data class AccountDepositResponse(
    val accountId: UUID,
    val amount: BigDecimal,
)