package com.tguimaraes.ledger.core.application.dto.account

import java.math.BigDecimal
import java.util.*

data class AccountBalanceResult(
    val accountId: UUID,
    val balance: BigDecimal
)