package com.tguimaraes.ledger.core.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class Transaction(
    val id: UUID,
    val amount: BigDecimal,
    val createdAt: Instant
)
