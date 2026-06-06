package com.tguimaraes.ledger.core.adapters.output.persistence.entity


import com.tguimaraes.ledger.core.domain.model.EntryType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

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