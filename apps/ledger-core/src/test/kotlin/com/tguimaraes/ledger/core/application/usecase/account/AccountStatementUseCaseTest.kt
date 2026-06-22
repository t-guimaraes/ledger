package com.tguimaraes.ledger.core.application.usecase.account

import com.tguimaraes.ledger.core.application.port.output.query.EntryQueryPort
import com.tguimaraes.ledger.core.application.port.output.repository.AccountRepositoryPort
import com.tguimaraes.ledger.core.application.usecase.AccountStatementUseCase
import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import com.tguimaraes.ledger.core.support.TestFixtures
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AccountStatementUseCaseTest {

    private lateinit var entryQueryPort: EntryQueryPort
    private lateinit var accountRepositoryPort: AccountRepositoryPort

    private lateinit var useCase: AccountStatementUseCase

    @BeforeEach
    fun setup() {
        entryQueryPort = mockk()
        accountRepositoryPort = mockk()

        useCase =
            AccountStatementUseCase(
                entryQueryPort,
                accountRepositoryPort
            )
    }

    @Test
    fun `should throw when account not exists`() {

        every {
            accountRepositoryPort.findById(TestFixtures.FROM_ACCOUNT_ID)
        } returns null

        Assertions.assertThrows(
            AccountNotFoundException::class.java
        ) {
            useCase.execute(TestFixtures.FROM_ACCOUNT_ID)
        }

        verify(exactly = 1) {
            accountRepositoryPort.findById(
                TestFixtures.FROM_ACCOUNT_ID
            )
        }
    }

    @Test
    fun `should return account statement`() {

        val accountId = TestFixtures.FROM_ACCOUNT_ID

        val entries = listOf( TestFixtures.statementEntryResult() )

        every {
            entryQueryPort.getStatement(TestFixtures.FROM_ACCOUNT_ID)
        } returns entries

        every {
            accountRepositoryPort.findById(TestFixtures.FROM_ACCOUNT_ID)
        } returns TestFixtures.fromAccount()

        val result = useCase.execute(TestFixtures.FROM_ACCOUNT_ID)

        Assertions.assertEquals(accountId, result.accountId)
        Assertions.assertEquals(1, result.entries.size)

        verify(exactly = 1) {
            entryQueryPort.getStatement(accountId)
        }
    }
}