package com.tguimaraes.ledger.core.application.usecase

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.CreateAccountDepositRequest
import com.tguimaraes.ledger.core.application.dto.CreateAccountDepositCommand
import com.tguimaraes.ledger.core.application.port.output.idempotency.IdempotencyPort
import com.tguimaraes.ledger.core.application.port.output.repository.AccountRepositoryPort
import com.tguimaraes.ledger.core.application.port.output.repository.EntryRepositoryPort
import com.tguimaraes.ledger.core.application.port.output.repository.TransactionRepositoryPort
import com.tguimaraes.ledger.core.domain.dto.DepositResult
import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import com.tguimaraes.ledger.core.domain.exception.IdempotencyException
import com.tguimaraes.ledger.core.domain.model.Account
import com.tguimaraes.ledger.core.domain.service.AccountDomainService
import com.tguimaraes.ledger.core.support.TestFixtures
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class CreateAccountDepositUseCaseTest {
    private val accountRepositoryPort = mockk<AccountRepositoryPort>()
    private val transactionRepositoryPort = mockk<TransactionRepositoryPort>()
    private val entryRepositoryPort = mockk<EntryRepositoryPort>()
    private val idempotencyPort = mockk<IdempotencyPort>()
    private val accountDomainService = mockk<AccountDomainService>()

    private lateinit var useCase: CreateAccountDepositUseCase

    private val amount = BigDecimal("1500.00")

    @BeforeEach
    fun setup() {
        useCase = CreateAccountDepositUseCase(
            accountRepositoryPort,
            transactionRepositoryPort,
            entryRepositoryPort,
            idempotencyPort,
            accountDomainService
        )
    }

    @Test
    fun `should throw when request already processed`() {

        every {
            idempotencyPort.exists(
                TestFixtures.IDEMPOTENCY_KEY
            )
        } returns false

        assertThrows(
            IdempotencyException::class.java
        ) {
            useCase.deposit(
                CreateAccountDepositCommand(amount),
                TestFixtures.FROM_ACCOUNT_ID,
                TestFixtures.IDEMPOTENCY_KEY
            )
        }

        verify(exactly = 1) {
            idempotencyPort.exists(
                TestFixtures.IDEMPOTENCY_KEY
            )
        }
    }

    @Test
    fun `should throw when account not exists`() {

        every {
            idempotencyPort.exists(TestFixtures.IDEMPOTENCY_KEY)
        } returns false

        every {
            accountRepositoryPort.findById(TestFixtures.FROM_ACCOUNT_ID)
        } returns null

        assertThrows(
            AccountNotFoundException::class.java
        ) {
            useCase.deposit(
                CreateAccountDepositCommand(amount),
                TestFixtures.FROM_ACCOUNT_ID,
                TestFixtures.IDEMPOTENCY_KEY
            )
        }

        verify(exactly = 1) {
            idempotencyPort.exists(
                TestFixtures.IDEMPOTENCY_KEY
            )
        }

        verify(exactly = 1) {
            accountRepositoryPort.findById(
                TestFixtures.FROM_ACCOUNT_ID
            )
        }
    }

    @Test
    fun `should persist deposit successfully`() {
        val depositResult = DepositResult(
            TestFixtures.transaction(),
            listOf(TestFixtures.creditEntry())
        )

        every {
            idempotencyPort.exists(TestFixtures.IDEMPOTENCY_KEY)
        } returns false

        every {
            accountRepositoryPort.findById(TestFixtures.FROM_ACCOUNT_ID)
        } returns TestFixtures.fromAccount()

        every {
            accountDomainService.createDeposit(TestFixtures.FROM_ACCOUNT_ID, amount)
        } returns depositResult

        every {
            transactionRepositoryPort.save(depositResult.transaction)
        } just runs

        every {
            entryRepositoryPort.saveAll(depositResult.entries)
        } just runs

        every {
            idempotencyPort.save(TestFixtures.IDEMPOTENCY_KEY)
        } just runs

        useCase.deposit(
            CreateAccountDepositCommand(amount),
            TestFixtures.FROM_ACCOUNT_ID,
            TestFixtures.IDEMPOTENCY_KEY
        )

        verify(exactly = 1) {
            idempotencyPort.exists(TestFixtures.IDEMPOTENCY_KEY)
        }

        verify(exactly = 1) {
            accountRepositoryPort.findById(TestFixtures.FROM_ACCOUNT_ID)
        }

        verify(exactly = 1) {
            accountDomainService.createDeposit(TestFixtures.FROM_ACCOUNT_ID, amount)
        }

        verify(exactly = 1) {
            transactionRepositoryPort.save(depositResult.transaction)
        }

        verify(exactly = 1) {
            entryRepositoryPort.saveAll(depositResult.entries)
        }

        verify(exactly = 1) {
            idempotencyPort.save(TestFixtures.IDEMPOTENCY_KEY)
        }

        confirmVerified(
            idempotencyPort,
            accountRepositoryPort,
            accountDomainService,
            transactionRepositoryPort,
            entryRepositoryPort
        )
    }
}