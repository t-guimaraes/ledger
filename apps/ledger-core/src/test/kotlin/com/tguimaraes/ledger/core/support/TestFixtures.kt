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

    val TRANSACTION_ID =
        UUID.fromString("33333333-3333-3333-3333-333333333333")

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

    fun defaultAccounts() =
        listOf(
            fromAccountEntity(),
            toAccountEntity()
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
        amount: BigDecimal = BigDecimal("100.00"),
        transactionId: UUID = TRANSACTION_ID,
    ) = Entry(
        id = UUID.randomUUID(),
        transactionId = transactionId,
        accountId = FROM_ACCOUNT_ID,
        type = EntryType.DEBIT,
        amount = amount,
        createdAt = CREATED_AT
    )

    fun creditEntry(
        amount: BigDecimal = BigDecimal("100.00"),
        transactionId: UUID = TRANSACTION_ID,
    ) = Entry(
        id = UUID.randomUUID(),
        transactionId = transactionId,
        accountId = TO_ACCOUNT_ID,
        type = EntryType.CREDIT,
        amount = amount,
        createdAt = CREATED_AT
    )

    fun debitEntryEntity(
        amount: BigDecimal = BigDecimal("100.00"),
        account: UUID = FROM_ACCOUNT_ID,
        transactionId: UUID = TRANSACTION_ID
    ) = EntryJpaEntity(
        id = UUID.randomUUID(),
        transactionId = transactionId,
        accountId = account,
        type = EntryType.DEBIT,
        amount = amount,
        createdAt = CREATED_AT
    )

    fun creditEntryEntity(
        amount: BigDecimal = BigDecimal("100.00"),
        account: UUID = TO_ACCOUNT_ID,
        transactionId: UUID = TRANSACTION_ID
    ) = EntryJpaEntity(
        id = UUID.randomUUID(),
        transactionId = transactionId,
        accountId = account,
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