package com.tguimaraes.ledger.core.integration.account.usecase

import com.tguimaraes.ledger.core.application.port.input.AccountBalanceInputPort
import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import com.tguimaraes.ledger.core.integration.support.AbstractIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.util.*

class AccountBalanceUseCaseIntegrationTest: AbstractIntegrationTest() {

    @Autowired
    private lateinit var accountBalanceInputPort: AccountBalanceInputPort

    @BeforeEach
    fun setup() {
        cleanDatabase()
        fromAccountId = UUID.randomUUID().also {
            createAccount(it, "Thiago")
            fundAccount(it,BigDecimal("1000.00"))
        }
    }

    @Test
    fun `should throw exception when account not exist`() {
        val randomAccount = UUID.randomUUID()
        assertThrows(AccountNotFoundException::class.java) {
            accountBalanceInputPort.execute(randomAccount)
        }
        assertFalse(accountRepository.existsById(randomAccount))
    }

    @Test
    fun `should get account balance successfully`() {
        val result = accountBalanceInputPort.execute(fromAccountId)

        assertThat(result.accountId).isEqualTo(fromAccountId)
        assertThat(result.balance).isEqualTo(BigDecimal("1000.00"))
    }
}