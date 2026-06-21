package com.tguimaraes.ledger.core.adapter.outbound.persistence.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.time.Instant
import java.util.*

@Entity
@Table(name = "accounts")
class AccountJpaEntity(

    @Id
    val id: UUID,

    val ownerName: String,

    val createdAt: Instant,

    val updatedAt: Instant?,

    @Version
    val version: Long = 0
)