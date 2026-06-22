package com.tguimaraes.ledger.core.integration.account.usecase

import com.tguimaraes.ledger.core.application.port.input.AccountBalanceInputPort
import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import com.tguimaraes.ledger.core.integration.support.AbstractIntegrationTest
import org.junit.jupiter.api.Assertions
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

        cleanEnvironment()

        fromAccountId = UUID.randomUUID()
        createAccount(fromAccountId, "Thiago")

        fundAccount(
            accountId = fromAccountId,
            amount = BigDecimal("1000.00")
        )
    }

    @Test
    fun `should throw when account not exist`() {
        val randomAccount = UUID.randomUUID()

        Assertions.assertThrows(AccountNotFoundException::class.java) {
            accountBalanceInputPort.execute(randomAccount)
        }
    }

    @Test
    fun `should get account balance successfully`() {
        var result = accountBalanceInputPort.execute(fromAccountId)

        Assertions.assertEquals(fromAccountId, result.accountId)
        Assertions.assertEquals(BigDecimal("1000.00"), result.balance)
    }
}