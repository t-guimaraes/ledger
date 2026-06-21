package com.tguimaraes.ledger.core.application.dto.transfer

import java.math.BigDecimal
import java.util.*

data class TransferCommand(
    val fromAccountId: UUID,
    val toAccountId: UUID,
    val amount: BigDecimal
)