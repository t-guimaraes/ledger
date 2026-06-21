package com.tguimaraes.ledger.core.adapter.outbound.persistence.entity


import com.tguimaraes.ledger.core.domain.model.EntryType
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Entity
@Table(name = "entries")
class EntryJpaEntity(

    @Id
    val id: UUID,

    val transactionId: UUID,

    val accountId: UUID,

    @Enumerated(EnumType.STRING)
    val type: EntryType,

    val amount: BigDecimal,

    val createdAt: Instant
)