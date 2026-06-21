package com.tguimaraes.ledger.core.domain.model

import com.tguimaraes.ledger.core.domain.exception.InvalidAccountOwnerNameException
import java.time.Instant
import java.util.*

data class Account(
    val id: UUID,
    val ownerName: String,
    val createdAt: Instant,
    val updatedAt: Instant?,
    val version: Long = 0
) {

    init {
        if (ownerName.isBlank()) {
            throw InvalidAccountOwnerNameException()
        }
    }

    companion object {

        fun normalizeOwnerName(ownerName: String): String {
            val normalized = ownerName.trim()

            if (normalized.isBlank()) {
                throw InvalidAccountOwnerNameException()
            }

            return normalized
        }

        fun create(
            id: UUID,
            ownerName: String,
            createdAt: Instant
        ) = Account(
            id = id,
            ownerName = normalizeOwnerName(ownerName),
            createdAt = createdAt,
            updatedAt = null,
            version = 0
        )
    }
}
