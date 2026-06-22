package com.tguimaraes.ledger.core.integration.account.usecase

import com.tguimaraes.ledger.core.application.dto.account.AccountDepositCommand
import com.tguimaraes.ledger.core.application.port.input.AccountDepositInputPort
import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import com.tguimaraes.ledger.core.domain.exception.IdempotencyException
import com.tguimaraes.ledger.core.domain.model.EntryType
import com.tguimaraes.ledger.core.integration.support.AbstractIntegrationTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.util.*

class AccountDepositUseCaseIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var accountDepositInputPort: AccountDepositInputPort

    private lateinit var accountDepositCommand: AccountDepositCommand

    @BeforeEach
    fun setup() {

        cleanEnvironment()

        fromAccountId = UUID.randomUUID()

        createAccount(
            fromAccountId,
            "Thiago"
        )

        accountDepositCommand = AccountDepositCommand(
            BigDecimal("1000.00")
        )
    }

    @Test
    fun `should throw when account not exist`() {

        val randomAccountId = UUID.randomUUID()

        assertThrows(AccountNotFoundException::class.java) {
            accountDepositInputPort.deposit(
                accountDepositCommand,
                randomAccountId,
                "integration-key"
            )
        }
    }

    @Test
    fun `should throw when idempotency key already exists`() {

        createIdempotencyKey("duplicate-key")

        assertThrows(IdempotencyException::class.java) {
            accountDepositInputPort.deposit(
                accountDepositCommand,
                fromAccountId,
                "duplicate-key"
            )
        }
    }

    @Test
    fun `should deposit successfully`() {

        val result = accountDepositInputPort.deposit(
            accountDepositCommand,
            fromAccountId,
            "integration-key"
        )

        assertEquals(
            fromAccountId,
            result.accountId
        )

        assertEquals(
            BigDecimal("1000.00"),
            result.amount
        )

        assertEquals(
            1,
            transactionRepository.count()
        )

        assertEquals(
            1,
            entryRepository.count()
        )

        val entry = entryRepository.findAll().first()

        assertEquals(
            fromAccountId,
            entry.accountId
        )

        assertEquals(
            EntryType.CREDIT,
            entry.type
        )

        assertEquals(
            BigDecimal("1000.00"),
            entry.amount
        )

        assertEquals(
            BigDecimal("1000.00"),
            entryRepository.getBalance(fromAccountId)
        )

        assertTrue(
            idempotencyRepository.existsById("integration-key")
        )
    }
}