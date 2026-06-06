package com.tguimaraes.ledger.core.domain.model

import java.time.Instant
import java.util.UUID

data class Account(
    val id: UUID,
    val ownerName: String,
    val createdAt: Instant,
    val updatedAt: Instant?,
    val version: Long = 0
)