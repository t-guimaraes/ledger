package com.tguimaraes.ledger.core.integration.account.usecase

import com.tguimaraes.ledger.core.application.usecase.AccountStatementUseCase
import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import com.tguimaraes.ledger.core.domain.model.EntryType
import com.tguimaraes.ledger.core.integration.support.AbstractIntegrationTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.util.*

class AccountStatementUseCaseIntegrationTest: AbstractIntegrationTest() {

    @Autowired
    private lateinit var accountStatementUseCase: AccountStatementUseCase

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
            accountStatementUseCase.execute(randomAccount)
        }
    }

    @Test
    fun `should get account statement successfully`() {
        fundAccount(
            accountId = fromAccountId,
            amount = BigDecimal("500.00")
        )

        val result = accountStatementUseCase.execute(fromAccountId)

        Assertions.assertEquals(fromAccountId, result.accountId)
        Assertions.assertEquals(2, result.entries.size)

        Assertions.assertEquals(EntryType.CREDIT, result.entries[0].type)
        Assertions.assertEquals(BigDecimal("500.00"), result.entries[0].amount)

        Assertions.assertEquals(EntryType.CREDIT, result.entries[0].type)
        Assertions.assertEquals(BigDecimal("1000.00"), result.entries[1].amount)
    }
}