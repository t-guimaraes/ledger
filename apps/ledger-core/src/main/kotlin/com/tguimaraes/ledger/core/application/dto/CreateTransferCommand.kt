package com.tguimaraes.ledger.core.application.dto

import java.math.BigDecimal
import java.util.UUID

data class CreateTransferCommand(
    val fromAccountId: UUID,
    val toAccountId: UUID,
    val amount: BigDecimal
)