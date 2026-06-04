package com.ledger.core.adapters.output.persistence.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "accounts")
class AccountJpaEntity(

    @Id
    val id: UUID,

    val ownerName: String,

    val createdAt: Instant,

    val version: Long
)