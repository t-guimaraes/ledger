package com.tguimaraes.ledger.core.adapter.outbound.persistence.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Entity
@Table(name = "transactions")
class TransactionJpaEntity(

    @Id
    val id: UUID,

    val amount: BigDecimal,

    val createdAt: Instant
)