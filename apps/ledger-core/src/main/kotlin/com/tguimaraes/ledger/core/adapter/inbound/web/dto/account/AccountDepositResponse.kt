package com.tguimaraes.ledger.core.adapter.inbound.web.dto.account

import java.math.BigDecimal
import java.util.*

data class AccountDepositResponse(
    val accountId: UUID,
    val amount: BigDecimal,
)