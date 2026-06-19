package com.tguimaraes.ledger.core.application.usecase

import com.tguimaraes.ledger.core.application.port.output.query.EntryQueryPort
import com.tguimaraes.ledger.core.support.TestFixtures
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class GetAccountBalanceUseCaseTest {

    private lateinit var entryQueryPort: EntryQueryPort

    private lateinit var useCase: GetAccountBalanceUseCase

    @BeforeEach
    fun setup() {
        entryQueryPort = mockk()

        useCase = GetAccountBalanceUseCase(
            entryQueryPort
        )
    }

    @Test
    fun `should return account balance`() {

        every {
            entryQueryPort.getBalance(
                TestFixtures.FROM_ACCOUNT_ID
            )
        } returns BigDecimal("1500.00")

        val result =
            useCase.execute(
                TestFixtures.FROM_ACCOUNT_ID
            )

        assertEquals(
            TestFixtures.FROM_ACCOUNT_ID,
            result.accountId
        )

        assertEquals(
            BigDecimal("1500.00"),
            result.balance
        )

        verify(exactly = 1) {
            entryQueryPort.getBalance(
                TestFixtures.FROM_ACCOUNT_ID
            )
        }
    }
}