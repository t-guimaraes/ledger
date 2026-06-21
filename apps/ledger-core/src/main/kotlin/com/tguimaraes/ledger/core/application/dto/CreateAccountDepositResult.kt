package com.tguimaraes.ledger.core.application.dto

import java.math.BigDecimal
import java.util.UUID

data class CreateAccountDepositResult(
    val accountId: UUID,
    val amount: BigDecimal
)