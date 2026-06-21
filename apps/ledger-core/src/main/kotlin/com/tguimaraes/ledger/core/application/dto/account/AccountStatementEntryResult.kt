package com.tguimaraes.ledger.core.application.dto.account

import com.tguimaraes.ledger.core.domain.model.EntryType
import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class AccountStatementEntryResult(
    val transactionId: UUID,
    val type: EntryType,
    val amount: BigDecimal,
    val createdAt: Instant
)