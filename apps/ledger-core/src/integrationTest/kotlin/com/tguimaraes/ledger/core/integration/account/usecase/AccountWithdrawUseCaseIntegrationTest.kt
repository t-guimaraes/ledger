package com.tguimaraes.ledger.core.integration.account.usecase

import com.tguimaraes.ledger.core.application.dto.account.AccountWithdrawCommand
import com.tguimaraes.ledger.core.application.port.input.AccountWithdrawInputPort
import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import com.tguimaraes.ledger.core.domain.exception.IdempotencyException
import com.tguimaraes.ledger.core.integration.support.AbstractIntegrationTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.util.*

class AccountWithdrawUseCaseIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var accountWithdrawInputPort: AccountWithdrawInputPort

    private lateinit var accountWithdrawCommand: AccountWithdrawCommand

    @BeforeEach
    fun setup() {

        cleanEnvironment()

        fromAccountId = UUID.randomUUID()

        createAccount(
            fromAccountId,
            "Thiago"
        )

        fundAccount(
            fromAccountId,
            BigDecimal("1000.00")
        )

        accountWithdrawCommand = AccountWithdrawCommand(
            BigDecimal("500.00")
        )
    }

    @Test
    fun `should throw when account not exist`() {

        val randomAccountId = UUID.randomUUID()

        assertThrows(AccountNotFoundException::class.java) {
            accountWithdrawInputPort.withdraw(
                accountWithdrawCommand,
                randomAccountId,
                "integration-key"
            )
        }
    }

    @Test
    fun `should throw when idempotency key already exists`() {

        createIdempotencyKey("duplicate-key")

        assertThrows(IdempotencyException::class.java) {
            accountWithdrawInputPort.withdraw(
                accountWithdrawCommand,
                fromAccountId,
                "duplicate-key"
            )
        }
    }

    @Test
    fun `should withdraw successfully`() {

        val result = accountWithdrawInputPort.withdraw(
            accountWithdrawCommand,
            fromAccountId,
            "integration-key"
        )

        assertEquals(
            fromAccountId,
            result.accountId
        )

        assertEquals(
            BigDecimal("500.00"),
            result.amount
        )

        assertTrue(
            idempotencyRepository.existsById("integration-key")
        )
    }
}