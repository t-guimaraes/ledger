package com.tguimaraes.ledger.core.application.usecase

import com.tguimaraes.ledger.core.application.port.output.query.EntryQueryPort
import com.tguimaraes.ledger.core.application.port.output.repository.AccountRepositoryPort
import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import com.tguimaraes.ledger.core.domain.model.Account
import com.tguimaraes.ledger.core.support.TestFixtures
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.util.Optional

class GetAccountBalanceUseCaseTest {

    private lateinit var entryQueryPort: EntryQueryPort
    private lateinit var accountRepositoryPort: AccountRepositoryPort
    private lateinit var useCase: GetAccountBalanceUseCase

    @BeforeEach
    fun setup() {
        entryQueryPort = mockk()
        accountRepositoryPort = mockk()

        useCase = GetAccountBalanceUseCase(
            entryQueryPort,
            accountRepositoryPort
        )
    }

    @Test
    fun `should return account balance`() {

        val account = Account(
            id = TestFixtures.FROM_ACCOUNT_ID,
            ownerName = "Thiago",
            createdAt = Instant.now(),
            updatedAt = null,
            version = 0
        )

        every {
            accountRepositoryPort.findById(TestFixtures.FROM_ACCOUNT_ID)
        } returns account

        every {
            entryQueryPort.getBalance(TestFixtures.FROM_ACCOUNT_ID)
        } returns BigDecimal("1500.00")

        val result = useCase.execute(TestFixtures.FROM_ACCOUNT_ID)

        assertEquals(TestFixtures.FROM_ACCOUNT_ID, result.accountId)
        assertEquals(BigDecimal("1500.00"), result.balance)

        verify(exactly = 1) {
            accountRepositoryPort.findById(TestFixtures.FROM_ACCOUNT_ID)
        }

        verify(exactly = 1) {
            entryQueryPort.getBalance(TestFixtures.FROM_ACCOUNT_ID)
        }
    }

    @Test
    fun `should throw when account does not exist when getting account balance`() {

        every {
            accountRepositoryPort.findById(TestFixtures.FROM_ACCOUNT_ID)
        } returns null

        assertThatThrownBy {
            useCase.execute(TestFixtures.FROM_ACCOUNT_ID)
        }
            .isInstanceOf(AccountNotFoundException::class.java)

        verify(exactly = 1) {
            accountRepositoryPort.findById(TestFixtures.FROM_ACCOUNT_ID)
        }

        verify(exactly = 0) {
            entryQueryPort.getBalance(any())
        }
    }
}