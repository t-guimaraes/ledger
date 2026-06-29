package com.tguimaraes.ledger.core.integration.account.usecase

import com.tguimaraes.ledger.core.application.dto.account.AccountWithdrawCommand
import com.tguimaraes.ledger.core.application.port.input.AccountWithdrawInputPort
import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import com.tguimaraes.ledger.core.domain.exception.IdempotencyException
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

class AccountWithdrawUseCaseIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var accountWithdrawInputPort: AccountWithdrawInputPort
    private lateinit var accountWithdrawCommand: AccountWithdrawCommand

    @BeforeEach
    fun setup() {
        cleanDatabase()
        accountWithdrawCommand = AccountWithdrawCommand(BigDecimal("500.00"))
        fromAccountId = UUID.randomUUID().also {
            createAccount(it, "Thiago")
            fundAccount(it, BigDecimal("1000.00"))
        }
    }

    @Test
    fun `should throw exception when idempotency key already exists`() {
        createIdempotencyKey("duplicate-key")
        assertThrows(IdempotencyException::class.java) {
            accountWithdrawInputPort.withdraw(accountWithdrawCommand, fromAccountId,"duplicate-key")
        }
        assertTrue(idempotencyRepository.existsById("duplicate-key"))
    }

    @Test
    fun `should throw exception when account not exist`() {
        val randomAccount = UUID.randomUUID()

        assertThrows(AccountNotFoundException::class.java) {
            accountWithdrawInputPort.withdraw(accountWithdrawCommand,randomAccount,"integration-key")
        }
        assertFalse(accountRepository.existsById(randomAccount))
    }

    @Test
    fun `should withdraw successfully`() {
        val result = accountWithdrawInputPort.withdraw(accountWithdrawCommand, fromAccountId,"integration-key")
        assertThat(result.accountId).isEqualTo(fromAccountId)
        assertThat(result.amount).isEqualTo(BigDecimal("500.00"))
        assertThat(outboxEventRepository.count()).isEqualTo(1)
        assertThat(transactionRepository.count()).isEqualTo(2)
        assertThat(entryRepository.count()).isEqualTo(2)

        assertThat(entryRepository.findAll())
            .extracting("amount", "type")
            .containsExactlyInAnyOrder(
                tuple(BigDecimal("1000.00"), EntryType.CREDIT),
                tuple(BigDecimal("500.00"), EntryType.DEBIT)
            )
    }
}