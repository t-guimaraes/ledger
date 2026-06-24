package com.tguimaraes.ledger.core.domain.event.transfer

import com.tguimaraes.ledger.core.domain.event.DomainEvent
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class TransferEvent(
    val transactionId: UUID,
    val fromAccountId: UUID,
    val toAccountId: UUID,
    val amount: BigDecimal,
    val occurredAt: Instant
) : DomainEvent