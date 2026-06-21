package com.tguimaraes.ledger.core.application.dto.account

import java.math.BigDecimal
import java.util.*

data class AccountWithdrawResult(
    val accountId: UUID,
    val amount: BigDecimal
)