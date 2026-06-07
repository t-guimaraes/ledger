package com.tguimaraes.ledger.core.support

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.CreateTransferRequest
import com.tguimaraes.ledger.core.application.dto.CreateTransferCommand
import com.tguimaraes.ledger.core.domain.model.Account
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

object TestFixtures {

    const val IDEMPOTENCY_KEY = "test-idempotency-key"

    val FROM_ACCOUNT_ID: UUID =
        UUID.fromString("11111111-1111-1111-1111-111111111111")

    val TO_ACCOUNT_ID: UUID =
        UUID.fromString("22222222-2222-2222-2222-222222222222")

    fun createTransferRequest(
        amount: BigDecimal = BigDecimal("100.00")
    ) = CreateTransferRequest(
        fromAccountId = FROM_ACCOUNT_ID,
        toAccountId = TO_ACCOUNT_ID,
        amount = amount
    )

    fun createTransferCommand(
        amount: BigDecimal = BigDecimal("100.00")
    ) = CreateTransferCommand(
        fromAccountId = FROM_ACCOUNT_ID,
        toAccountId = TO_ACCOUNT_ID,
        amount = amount
    )

    fun fromAccount() =
        Account(
            id = FROM_ACCOUNT_ID,
            ownerName = "Thiago",
            createdAt = Instant.now(),
            version = 0,
            updatedAt = Instant.now()
        )

    fun toAccount() =
        Account(
            id = TO_ACCOUNT_ID,
            ownerName = "Maria",
            createdAt = Instant.now(),
            version = 0,
            updatedAt = Instant.now()
        )
}