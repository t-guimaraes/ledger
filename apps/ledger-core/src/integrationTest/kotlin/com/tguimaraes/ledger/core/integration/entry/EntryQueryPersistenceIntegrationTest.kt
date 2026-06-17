package com.tguimaraes.ledger.core.integration.entry

import com.tguimaraes.ledger.core.adapter.outbound.persistence.entity.AccountJpaEntity
import com.tguimaraes.ledger.core.adapter.outbound.persistence.entity.EntryJpaEntity
import com.tguimaraes.ledger.core.adapter.outbound.persistence.entity.TransactionJpaEntity
import com.tguimaraes.ledger.core.domain.model.EntryType
import com.tguimaraes.ledger.core.integration.AbstractIntegrationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class EntryQueryPersistenceIntegrationTest : AbstractIntegrationTest() {

    private lateinit var accountId: UUID
    private lateinit var transactionId: UUID

    @BeforeEach
    fun setup() {

        cleanEnvironment()

        accountId = UUID.randomUUID()
        transactionId = UUID.randomUUID()

        createAccount(accountId, "Main Account")

        transactionRepository.save(
            TransactionJpaEntity(
                id = transactionId,
                amount = BigDecimal("1000.00"),
                createdAt = Instant.now()
            )
        )
    }

    @Test
    fun `should return zero when account has no entries`() {

        val balance = entryRepository.getBalance(accountId)

        assertEquals(BigDecimal.ZERO, balance)
    }

    @Test
    fun `should calculate balance correctly with credits and debits`() {

        entryRepository.saveAll(
            listOf(
                creditEntry(
                    amount = BigDecimal("1000.00"),
                    accountId = accountId
                ),
                debitEntry(
                    amount = BigDecimal("250.00"),
                    accountId = accountId
                )
            )
        )

        val balance = entryRepository.getBalance(accountId)

        assertEquals(BigDecimal("750.00"), balance)
    }

    @Test
    fun `should ignore entries from other accounts`() {

        val otherAccountId = UUID.randomUUID()

        accountRepository.save(
            AccountJpaEntity(
                id = otherAccountId,
                ownerName = "Other Account",
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
                version = 0
            )
        )

        entryRepository.saveAll(
            listOf(
                creditEntry(
                    amount = BigDecimal("1000.00"),
                    accountId = accountId
                ),
                creditEntry(
                    amount = BigDecimal("999999.00"),
                    accountId = otherAccountId
                )
            )
        )

        val balance = entryRepository.getBalance(accountId)

        assertEquals(BigDecimal("1000.00"), balance)
    }

    @Test
    fun `should calculate negative balance correctly`() {

        entryRepository.save(
            debitEntry(
                amount = BigDecimal("500.00"),
                accountId = accountId
            )
        )

        val balance = entryRepository.getBalance(accountId)

        assertEquals(BigDecimal("-500.00"), balance)
    }

    @Test
    fun `should calculate balance with multiple entries`() {

        entryRepository.saveAll(
            listOf(
                creditEntry(
                    amount = BigDecimal("1000.00"),
                    accountId = accountId
                ),
                creditEntry(
                    amount = BigDecimal("500.00"),
                    accountId = accountId
                ),
                debitEntry(
                    amount = BigDecimal("300.00"),
                    accountId = accountId
                ),
                debitEntry(
                    amount = BigDecimal("200.00"),
                    accountId = accountId
                )
            )
        )

        val balance = entryRepository.getBalance(accountId)

        assertEquals(BigDecimal("1000.00"), balance)
    }

    private fun creditEntry(
        amount: BigDecimal,
        accountId: UUID
    ): EntryJpaEntity =
        EntryJpaEntity(
            id = UUID.randomUUID(),
            transactionId = transactionId,
            accountId = accountId,
            amount = amount,
            type = EntryType.CREDIT,
            createdAt = Instant.now()
        )

    private fun debitEntry(
        amount: BigDecimal,
        accountId: UUID
    ): EntryJpaEntity =
        EntryJpaEntity(
            id = UUID.randomUUID(),
            transactionId = transactionId,
            accountId = accountId,
            amount = amount,
            type = EntryType.DEBIT,
            createdAt = Instant.now()
        )
}