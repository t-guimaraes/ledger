package com.tguimaraes.ledger.core.integration.account.usecase

import com.tguimaraes.ledger.core.application.dto.account.AccountDepositCommand
import com.tguimaraes.ledger.core.application.port.input.AccountDepositInputPort
import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import com.tguimaraes.ledger.core.domain.exception.IdempotencyException
import com.tguimaraes.ledger.core.domain.model.EntryType
import com.tguimaraes.ledger.core.integration.support.AbstractIntegrationTest
import org.assertj.core.api.Assertions.assertThat
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
        cleanDatabase()
        accountDepositCommand = AccountDepositCommand(BigDecimal("1000.00"))
        fromAccountId = UUID.randomUUID().also {
            createAccount(it, "Thiago")
        }
    }

    @Test
    fun `should throw exception when idempotency key already exists`() {
        createIdempotencyKey("duplicate-key")
        assertThrows(IdempotencyException::class.java) {
            accountDepositInputPort.deposit(accountDepositCommand, fromAccountId,"duplicate-key")
        }
        assertTrue(idempotencyRepository.existsById("duplicate-key"))
    }

    @Test
    fun `should throw exception when account not exist`() {
        val randomAccount = UUID.randomUUID()
        assertThrows(AccountNotFoundException::class.java) {
            accountDepositInputPort.deposit(accountDepositCommand,randomAccount,"integration-key")
        }
        assertFalse(accountRepository.existsById(randomAccount))
    }

    @Test
    fun `should deposit successfully`() {
        val result = accountDepositInputPort.deposit(accountDepositCommand, fromAccountId,"integration-key")
        assertThat(result.accountId).isEqualTo(fromAccountId)
        assertThat(result.amount).isEqualTo(BigDecimal("1000.00"))
        assertThat(outboxEventRepository.count()).isEqualTo(1)
        assertThat(transactionRepository.count()).isEqualTo(1)
        assertThat(entryRepository.count()).isEqualTo(1)

        val entry = entryRepository.findAll().first()
        assertThat(entry.accountId).isEqualTo(fromAccountId)
        assertThat(entry.type).isEqualTo(EntryType.CREDIT)
        assertThat(entry.amount).isEqualTo(BigDecimal("1000.00"))
        assertTrue(idempotencyRepository.existsById("integration-key"))
    }
}