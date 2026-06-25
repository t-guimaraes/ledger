package com.tguimaraes.ledger.core.domain.event.account

import com.tguimaraes.ledger.core.domain.event.DomainEvent
import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class AccountWithdrawEvent(
    val transactionId: UUID,
    val accountId: UUID,
    val amount: BigDecimal,
    val occurredAt: Instant
) : DomainEvent