package com.tguimaraes.ledger.core.application.dto

import java.math.BigDecimal
import java.util.UUID

data class CreateAccountDepositCommand(
    val amount: BigDecimal
)