package com.tguimaraes.ledger.core.support

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.CreateTransferRequest
import com.tguimaraes.ledger.core.adapter.outbound.persistence.entity.AccountJpaEntity
import com.tguimaraes.ledger.core.adapter.outbound.persistence.entity.EntryJpaEntity
import com.tguimaraes.ledger.core.adapter.outbound.persistence.entity.TransactionJpaEntity
import com.tguimaraes.ledger.core.application.dto.AccountBalanceResult
import com.tguimaraes.ledger.core.application.dto.CreateTransferCommand
import com.tguimaraes.ledger.core.domain.model.Account
import com.tguimaraes.ledger.core.domain.model.Entry
import com.tguimaraes.ledger.core.domain.model.EntryType
import com.tguimaraes.ledger.core.domain.model.Transaction
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

object TestFixtures {

    const val IDEMPOTENCY_KEY = "test-idempotency-key"

    val CREATED_AT: Instant =
        Instant.parse("2025-01-01T00:00:00Z")

    val UPDATED_AT: Instant =
        Instant.parse("2025-01-01T00:00:00Z")

    val FROM_ACCOUNT_ID: UUID =
        UUID.fromString("11111111-1111-1111-1111-111111111111")

    val TO_ACCOUNT_ID: UUID =
        UUID.fromString("22222222-2222-2222-2222-222222222222")

    val TRANSACTION_ID: UUID =
        UUID.fromString("33333333-3333-3333-3333-333333333333")

    val DEBIT_ENTRY_ID: UUID =
        UUID.fromString("44444444-4444-4444-4444-444444444444")

    val CREDIT_ENTRY_ID: UUID =
        UUID.fromString("55555555-5555-5555-5555-555555555555")

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
            createdAt = CREATED_AT,
            updatedAt = UPDATED_AT,
            version = 0
        )

    fun toAccount() =
        Account(
            id = TO_ACCOUNT_ID,
            ownerName = "Maria",
            createdAt = CREATED_AT,
            updatedAt = UPDATED_AT,
            version = 0
        )

    fun transaction(
        amount: BigDecimal = BigDecimal("100.00")
    ) = Transaction(
        id = TRANSACTION_ID,
        amount = amount,
        createdAt = CREATED_AT
    )

    fun transactionEntity(
        amount: BigDecimal = BigDecimal("100.00")
    ) = TransactionJpaEntity(
        id = TRANSACTION_ID,
        amount = amount,
        createdAt = CREATED_AT
    )

    fun debitEntry(
        amount: BigDecimal = BigDecimal("100.00")
    ) = Entry(
        id = DEBIT_ENTRY_ID,
        transactionId = TRANSACTION_ID,
        accountId = FROM_ACCOUNT_ID,
        type = EntryType.DEBIT,
        amount = amount,
        createdAt = CREATED_AT
    )

    fun creditEntry(
        amount: BigDecimal = BigDecimal("100.00")
    ) = Entry(
        id = CREDIT_ENTRY_ID,
        transactionId = TRANSACTION_ID,
        accountId = TO_ACCOUNT_ID,
        type = EntryType.CREDIT,
        amount = amount,
        createdAt = CREATED_AT
    )

    fun debitEntryEntity(
        amount: BigDecimal = BigDecimal("100.00")
    ) = EntryJpaEntity(
        id = DEBIT_ENTRY_ID,
        transactionId = TRANSACTION_ID,
        accountId = FROM_ACCOUNT_ID,
        type = EntryType.DEBIT,
        amount = amount,
        createdAt = CREATED_AT
    )

    fun creditEntryEntity(
        amount: BigDecimal = BigDecimal("100.00")
    ) = EntryJpaEntity(
        id = CREDIT_ENTRY_ID,
        transactionId = TRANSACTION_ID,
        accountId = TO_ACCOUNT_ID,
        type = EntryType.CREDIT,
        amount = amount,
        createdAt = CREATED_AT
    )

    fun fromAccountEntity() =
        AccountJpaEntity(
            id = FROM_ACCOUNT_ID,
            ownerName = "Thiago",
            createdAt = CREATED_AT,
            updatedAt = UPDATED_AT,
            version = 0
        )


    fun toAccountEntity() =
        AccountJpaEntity(
            id = TO_ACCOUNT_ID,
            ownerName = "Maria",
            createdAt = CREATED_AT,
            updatedAt = UPDATED_AT,
            version = 0
        )

    fun accountBalance(
        balance: BigDecimal = BigDecimal("1500.00")
    ) = AccountBalanceResult(
        accountId = FROM_ACCOUNT_ID,
        balance = balance
    )
}