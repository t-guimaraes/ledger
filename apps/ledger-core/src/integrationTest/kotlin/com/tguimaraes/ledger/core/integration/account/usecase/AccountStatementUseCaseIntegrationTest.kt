package com.tguimaraes.ledger.core.integration.account.usecase

import com.tguimaraes.ledger.core.application.port.input.AccountStatementInputPort
import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import com.tguimaraes.ledger.core.domain.model.EntryType
import com.tguimaraes.ledger.core.integration.support.AbstractIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.util.*

class AccountStatementUseCaseIntegrationTest: AbstractIntegrationTest() {

    @Autowired
    private lateinit var accountStatementInputPort: AccountStatementInputPort

    @BeforeEach
    fun setup() {
        cleanDatabase()
        fromAccountId = UUID.randomUUID().also {
            createAccount(it, "Thiago")
            fundAccount(it,BigDecimal("1000.00"))
            fundAccount(it,BigDecimal("500.00"))
        }
    }

    @Test
    fun `should throw exception when account not exist`() {
        val randomAccount = UUID.randomUUID()

        assertThrows(AccountNotFoundException::class.java) {
            accountStatementInputPort.execute(randomAccount)
        }
    }

    @Test
    fun `should get account balance successfully`() {
        val result = accountStatementInputPort.execute(fromAccountId)

        assertThat(result.accountId).isEqualTo(fromAccountId)
        assertThat(result.entries).size().isEqualTo(2)
        assertThat(result.entries)
            .extracting("amount", "type")
            .containsExactlyInAnyOrder(
                tuple(BigDecimal("1000.00"), EntryType.CREDIT),
                tuple(BigDecimal("500.00"), EntryType.CREDIT)
            )
    }
}