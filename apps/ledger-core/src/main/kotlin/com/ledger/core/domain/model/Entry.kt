package com.ledger.core.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class Entry(
    val id: UUID,
    val transactionId: UUID,
    val accountId: UUID,
    val type: EntryType,
    val amount: BigDecimal,
    val createdAt: Instant
)
