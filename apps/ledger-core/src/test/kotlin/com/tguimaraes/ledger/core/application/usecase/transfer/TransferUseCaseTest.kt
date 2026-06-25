package com.tguimaraes.ledger.core.application.usecase.transfer

import com.tguimaraes.ledger.core.application.port.output.event.EventPublisherPort
import com.tguimaraes.ledger.core.application.port.output.idempotency.IdempotencyPort
import com.tguimaraes.ledger.core.application.port.output.query.EntryQueryPort
import com.tguimaraes.ledger.core.application.port.output.repository.AccountRepositoryPort
import com.tguimaraes.ledger.core.application.port.output.repository.EntryRepositoryPort
import com.tguimaraes.ledger.core.application.port.output.repository.TransactionRepositoryPort
import com.tguimaraes.ledger.core.application.usecase.TransferUseCase
import com.tguimaraes.ledger.core.domain.dto.TransferResult
import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import com.tguimaraes.ledger.core.domain.exception.IdempotencyException
import com.tguimaraes.ledger.core.domain.model.Entry
import com.tguimaraes.ledger.core.domain.model.Transaction
import com.tguimaraes.ledger.core.domain.service.TransferDomainService
import com.tguimaraes.ledger.core.support.TestFixtures
import io.mockk.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.util.*

class TransferUseCaseTest {
    private lateinit var accountRepositoryPort: AccountRepositoryPort
    private lateinit var transactionRepositoryPort: TransactionRepositoryPort
    private lateinit var entryRepositoryPort: EntryRepositoryPort
    private lateinit var entryQueryPort: EntryQueryPort
    private lateinit var idempotencyPort: IdempotencyPort
    private val eventPublisherPort = mockk<EventPublisherPort>()
    private lateinit var transferDomainService: TransferDomainService

    private lateinit var useCase: TransferUseCase

    @BeforeEach
    fun setup() {

        accountRepositoryPort = mockk()
        transactionRepositoryPort = mockk()
        entryRepositoryPort = mockk()
        entryQueryPort = mockk()
        idempotencyPort = mockk()
        transferDomainService = mockk()

        useCase = TransferUseCase(
            accountRepositoryPort,
            transactionRepositoryPort,
            entryRepositoryPort,
            entryQueryPort,
            idempotencyPort,
            eventPublisherPort,
            transferDomainService
        )
    }

    @Test
    fun `should throw when request already processed`() {

        every {
            idempotencyPort.exists(
                TestFixtures.IDEMPOTENCY_KEY
            )
        } returns true

        Assertions.assertThrows(
            IdempotencyException::class.java
        ) {
            useCase.transfer(
                TestFixtures.createTransferCommand(),
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
    fun `should throw when source account does not exist`() {

        every {
            idempotencyPort.exists(any())
        } returns false

        every {
            accountRepositoryPort.findById(
                TestFixtures.FROM_ACCOUNT_ID
            )
        } returns null

        Assertions.assertThrows(
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
            idempotencyPort.exists(any())
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

        Assertions.assertThrows(
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
            idempotencyPort.exists(any())
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
            idempotencyPort.save(TestFixtures.IDEMPOTENCY_KEY)
        } just runs

        every {
            eventPublisherPort.publish(any())
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
            idempotencyPort.exists(
                TestFixtures.IDEMPOTENCY_KEY
            )
        }

        verify(exactly = 1) {
            idempotencyPort.save(
                TestFixtures.IDEMPOTENCY_KEY
            )
        }

        confirmVerified(
            transactionRepositoryPort,
            entryRepositoryPort,
            idempotencyPort
        )
    }
}