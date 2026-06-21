package com.tguimaraes.ledger.core.application.usecase.account

import com.tguimaraes.ledger.core.application.port.output.query.EntryQueryPort
import com.tguimaraes.ledger.core.application.usecase.AccountStatementUseCase
import com.tguimaraes.ledger.core.support.TestFixtures
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AccountStatementUseCaseTest {

    private lateinit var entryQueryPort: EntryQueryPort

    private lateinit var useCase: AccountStatementUseCase

    @BeforeEach
    fun setup() {
        entryQueryPort = mockk()

        useCase =
            AccountStatementUseCase(
                entryQueryPort
            )
    }

    @Test
    fun `should return account statement`() {

        val accountId = TestFixtures.FROM_ACCOUNT_ID

        val entries = listOf( TestFixtures.statementEntryResult() )

        every {
            entryQueryPort.getStatement(TestFixtures.FROM_ACCOUNT_ID)
        } returns entries

        val result = useCase.execute(TestFixtures.FROM_ACCOUNT_ID)

        Assertions.assertEquals(accountId, result.accountId)
        Assertions.assertEquals(1, result.entries.size)

        verify(exactly = 1) {
            entryQueryPort.getStatement(accountId)
        }
    }
}