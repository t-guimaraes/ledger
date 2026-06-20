package com.tguimaraes.ledger.core.adapter.outbound.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "idempotency_keys")
class IdempotencyKeyJpaEntity(

    @Id
    @Column(name = "idempotency_key")
    val key: String,

    val createdAt: Instant
)
