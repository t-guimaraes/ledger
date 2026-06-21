package com.tguimaraes.ledger.core.application.dto.account

import java.math.BigDecimal

data class AccountWithdrawCommand(
    val amount: BigDecimal
)