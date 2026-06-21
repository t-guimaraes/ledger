package com.tguimaraes.ledger.core.integration.transfer

import com.tguimaraes.ledger.core.application.dto.transfer.TransferCommand
import com.tguimaraes.ledger.core.application.port.input.TransferInputPort
import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import com.tguimaraes.ledger.core.domain.exception.IdempotencyException
import com.tguimaraes.ledger.core.domain.exception.InsufficientBalanceException
import com.tguimaraes.ledger.core.domain.model.EntryType
import com.tguimaraes.ledger.core.integration.support.AbstractIntegrationTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.util.*

class TransferUseCaseIntegrationTest: AbstractIntegrationTest() {

    @Autowired
    private lateinit var transferInputPort: TransferInputPort

    @BeforeEach
    fun setup() {

        cleanEnvironment()

        fromAccountId = UUID.randomUUID()
        toAccountId = UUID.randomUUID()

        createAccount(fromAccountId, "Thiago")
        createAccount(toAccountId, "Maria")

        fundAccount(
            accountId = fromAccountId,
            amount = BigDecimal("1000.00")
        )
    }

    @Test
    fun `should create transfer successfully`() {

        transferInputPort.transfer(
            TransferCommand(
                fromAccountId = fromAccountId,
                toAccountId = toAccountId,
                amount = BigDecimal("200.00")
            ),
            "integration-key"
        )

        assertEquals(
            2,
            transactionRepository.count()
        )

        val entries = entryRepository.findAll()

        assertEquals(
            1,
            entries.count { it.type == EntryType.DEBIT }
        )

        assertEquals(
            2,
            entries.count { it.type == EntryType.CREDIT }
        )

        val fromBalance =
            entryRepository.getBalance(fromAccountId)

        val toBalance =
            entryRepository.getBalance(toAccountId)

        assertEquals(
            BigDecimal("800.00"),
            fromBalance
        )

        assertEquals(
            BigDecimal("200.00"),
            toBalance
        )

        assertTrue(
            idempotencyRepository.existsById("integration-key")
        )
    }

    @Test
    fun `should throw exception when idempotency key already exists`() {

        createIdempotencyKey("duplicate-key")

        assertThrows(IdempotencyException::class.java) {
            transferInputPort.transfer(
                TransferCommand(
                    fromAccountId = fromAccountId,
                    toAccountId = toAccountId,
                    amount = BigDecimal("100.00")
                ),
                "duplicate-key"
            )
        }

        assertEquals(1, transactionRepository.count())
        assertEquals(1, entryRepository.count())
    }

    @Test
    fun `should throw exception when source account does not exist`() {

        assertThrows(AccountNotFoundException::class.java) {
            transferInputPort.transfer(
                TransferCommand(
                    fromAccountId = UUID.randomUUID(),
                    toAccountId = toAccountId,
                    amount = BigDecimal("100.00")
                ),
                "key"
            )
        }
    }

    @Test
    fun `should throw exception when destination account does not exist`() {

        assertThrows(AccountNotFoundException::class.java) {
            transferInputPort.transfer(
                TransferCommand(
                    fromAccountId = fromAccountId,
                    toAccountId = UUID.randomUUID(),
                    amount = BigDecimal("100.00")
                ),
                "key"
            )
        }
    }

    @Test
    fun `should throw exception when balance is insufficient`() {

        assertThrows(InsufficientBalanceException::class.java) {
            transferInputPort.transfer(
                TransferCommand(
                    fromAccountId = fromAccountId,
                    toAccountId = toAccountId,
                    amount = BigDecimal("2000.00")
                ),
                "key"
            )
        }

        assertEquals(
            1,
            transactionRepository.count()
        )

        assertEquals(
            1,
            entryRepository.count()
        )
    }
}
