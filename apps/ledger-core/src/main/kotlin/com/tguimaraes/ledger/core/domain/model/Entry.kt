package com.tguimaraes.ledger.core.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class Entry(
    val id: UUID,
    val transactionId: UUID,
    val accountId: UUID,
    val type: EntryType,
    val amount: BigDecimal,
    val createdAt: Instant
)
