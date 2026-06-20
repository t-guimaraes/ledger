package com.tguimaraes.ledger.core.application.usecase

import com.tguimaraes.ledger.core.application.port.output.repository.AccountRepositoryPort
import com.tguimaraes.ledger.core.application.port.output.query.EntryQueryPort
import com.tguimaraes.ledger.core.application.port.output.repository.EntryRepositoryPort
import com.tguimaraes.ledger.core.application.port.output.cache.IdempotencyCachePort
import com.tguimaraes.ledger.core.application.port.output.repository.TransactionRepositoryPort
import com.tguimaraes.ledger.core.domain.dto.TransferResult
import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import com.tguimaraes.ledger.core.domain.exception.IdempotencyException
import com.tguimaraes.ledger.core.domain.model.Entry
import com.tguimaraes.ledger.core.domain.model.Transaction
import com.tguimaraes.ledger.core.domain.service.TransferDomainService
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
import java.time.Instant
import java.util.UUID

class CreateTransferUseCaseTest {
    private lateinit var accountRepositoryPort: AccountRepositoryPort
    private lateinit var transactionRepositoryPort: TransactionRepositoryPort
    private lateinit var entryRepositoryPort: EntryRepositoryPort
    private lateinit var entryQueryPort: EntryQueryPort
    private lateinit var idempotencyCachePort: IdempotencyCachePort
    private lateinit var transferDomainService: TransferDomainService

    private lateinit var useCase: CreateTransferUseCase

    @BeforeEach
    fun setup() {

        accountRepositoryPort = mockk()
        transactionRepositoryPort = mockk()
        entryRepositoryPort = mockk()
        entryQueryPort = mockk()
        idempotencyCachePort = mockk()
        transferDomainService = mockk()

        useCase = CreateTransferUseCase(
            accountRepositoryPort,
            transactionRepositoryPort,
            entryRepositoryPort,
            entryQueryPort,
            idempotencyCachePort,
            transferDomainService
        )
    }

    @Test
    fun `should throw when request already processed`() {

        every {
            idempotencyCachePort.exists(
                TestFixtures.IDEMPOTENCY_KEY
            )
        } returns true

        assertThrows(
            IdempotencyException::class.java
        ) {
            useCase.transfer(
                TestFixtures.createTransferCommand(),
                TestFixtures.IDEMPOTENCY_KEY
            )
        }

        verify(exactly = 1) {
            idempotencyCachePort.exists(
                TestFixtures.IDEMPOTENCY_KEY
            )
        }
    }

    @Test
    fun `should throw when source account does not exist`() {

        every {
            idempotencyCachePort.exists(any())
        } returns false

        every {
            accountRepositoryPort.findById(
                TestFixtures.FROM_ACCOUNT_ID
            )
        } returns null

        assertThrows(
            AccountNotFoundException::class.java
        ) {
            useCase.transfer(
                TestFixtures.createTransferCommand(),
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
    fun `should throw when destination account does not exist`() {

        every {
            idempotencyCachePort.exists(any())
        } returns false

        every {
            accountRepositoryPort.findById(
                TestFixtures.FROM_ACCOUNT_ID
            )
        } returns TestFixtures.fromAccount()

        every {
            accountRepositoryPort.findById(
                TestFixtures.TO_ACCOUNT_ID
            )
        } returns null

        assertThrows(
            AccountNotFoundException::class.java
        ) {
            useCase.transfer(
                TestFixtures.createTransferCommand(),
                TestFixtures.IDEMPOTENCY_KEY
            )
        }

        verify(exactly = 1) {
            accountRepositoryPort.findById(
                TestFixtures.FROM_ACCOUNT_ID
            )
        }

        verify(exactly = 1) {
            accountRepositoryPort.findById(
                TestFixtures.TO_ACCOUNT_ID
            )
        }
    }

    @Test
    fun `should persist transfer successfully`() {

        val command =
            TestFixtures.createTransferCommand()

        val transaction = Transaction(
            id = UUID.randomUUID(),
            amount = command.amount,
            createdAt = Instant.now()
        )

        val entries = emptyList<Entry>()

        val transferResult = TransferResult(
            transaction = transaction,
            entries = entries
        )

        every {
            idempotencyCachePort.exists(any())
        } returns false

        every {
            accountRepositoryPort.findById(
                TestFixtures.FROM_ACCOUNT_ID
            )
        } returns TestFixtures.fromAccount()

        every {
            accountRepositoryPort.findById(
                TestFixtures.TO_ACCOUNT_ID
            )
        } returns TestFixtures.toAccount()

        every {
            entryQueryPort.getBalance(
                TestFixtures.FROM_ACCOUNT_ID
            )
        } returns BigDecimal("500.00")

        every {
            transferDomainService.createTransfer(
                any(),
                any(),
                any(),
                any()
            )
        } returns transferResult
        every {
            transactionRepositoryPort.save(transaction)
        } just runs
        every {
            entryRepositoryPort.saveAll(entries)
        } just runs
        every {
            idempotencyCachePort.save(TestFixtures.IDEMPOTENCY_KEY)
        } just runs

        useCase.transfer(
            command,
            TestFixtures.IDEMPOTENCY_KEY
        )

        verify(exactly = 1) {
            transactionRepositoryPort.save(
                transaction
            )
        }

        verify(exactly = 1) {
            entryRepositoryPort.saveAll(
                entries
            )
        }

        verify(exactly = 1) {
            idempotencyCachePort.exists(
                TestFixtures.IDEMPOTENCY_KEY
            )
        }

        verify(exactly = 1) {
            idempotencyCachePort.save(
                TestFixtures.IDEMPOTENCY_KEY
            )
        }

        confirmVerified(
            transactionRepositoryPort,
            entryRepositoryPort,
            idempotencyCachePort
        )
    }
}
