package com.tguimaraes.ledger.core.integration.transfer.usecase

import com.tguimaraes.ledger.core.application.dto.transfer.TransferCommand
import com.tguimaraes.ledger.core.application.port.input.TransferInputPort
import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import com.tguimaraes.ledger.core.domain.exception.IdempotencyException
import com.tguimaraes.ledger.core.domain.exception.InsufficientBalanceException
import com.tguimaraes.ledger.core.domain.exception.InvalidTransferAmountException
import com.tguimaraes.ledger.core.domain.model.EntryType
import com.tguimaraes.ledger.core.integration.support.AbstractIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
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
        cleanDatabase()
        fromAccountId = UUID.randomUUID().also {
            createAccount(it, "Thiago")
            fundAccount(it, BigDecimal("1000.00"))
        }
        toAccountId = UUID.randomUUID().also {
            createAccount(it, "Maria")
        }
    }

    @Test
    fun `should throw exception when idempotency key already exists`() {
        createIdempotencyKey("duplicate-key")
        val command = TransferCommand(fromAccountId, toAccountId, BigDecimal("200.00"))

        assertThrows(IdempotencyException::class.java) {
            transferInputPort.transfer(command,"duplicate-key")
        }

        assertTrue(idempotencyRepository.existsById("duplicate-key"))
    }

    @Test
    fun `should throw exception when source account does not exist`() {
        val randomAccount = UUID.randomUUID()
        val command = TransferCommand(randomAccount, toAccountId, BigDecimal("200.00"))

        assertThrows(AccountNotFoundException::class.java) {
            transferInputPort.transfer(command,"integration-key")
        }

        assertFalse(accountRepository.existsById(randomAccount))
    }

    @Test
    fun `should throw exception when destination account does not exist`() {
        val randomAccount = UUID.randomUUID()
        val command = TransferCommand(fromAccountId, randomAccount, BigDecimal("200.00"))

        assertThrows(AccountNotFoundException::class.java) {
            transferInputPort.transfer(command,"integration-key")
        }

        assertFalse(accountRepository.existsById(randomAccount))
    }

    @Test
    fun `should throw exception when amount is less or equal 0`() {
        val command = TransferCommand(fromAccountId, toAccountId, BigDecimal.ZERO)

        assertThrows(InvalidTransferAmountException::class.java) {
            transferInputPort.transfer(command, "integration-key"
            )
        }
    }

    @Test
    fun `should throw exception when balance is insufficient`() {
        val command = TransferCommand(fromAccountId, toAccountId, BigDecimal("2000.00"))

        assertThrows(InsufficientBalanceException::class.java) {
            transferInputPort.transfer(command, "integration-key"
            )
        }

        assertThat(entryRepository.getBalance(fromAccountId)).isLessThan(BigDecimal("2000.00"))
    }

    @Test
    fun `should create transfer successfully`() {
        val command = TransferCommand(fromAccountId, toAccountId, BigDecimal("300.00"))

        transferInputPort.transfer(command,"integration-key")

        assertTrue(idempotencyRepository.existsById("integration-key"))
        assertThat(transactionRepository.count()).isEqualTo(2)
        assertThat(entryRepository.count()).isEqualTo(3)
        assertThat(outboxEventRepository.count()).isEqualTo(1)

        assertThat(entryRepository.getBalance(fromAccountId)).isEqualTo(BigDecimal("700.00"))
        assertThat(entryRepository.getBalance(toAccountId)).isEqualTo(BigDecimal("300.00"))

        assertThat(entryRepository.findAll())
            .extracting("accountId", "amount", "type")
            .containsExactlyInAnyOrder(
                tuple(fromAccountId, BigDecimal("1000.00"), EntryType.CREDIT),
                tuple(fromAccountId, BigDecimal("300.00"), EntryType.DEBIT),
                tuple(toAccountId, BigDecimal("300.00"), EntryType.CREDIT)
            )
    }
}