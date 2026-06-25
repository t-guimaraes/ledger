package com.tguimaraes.ledger.core.application.usecase.account

import com.tguimaraes.ledger.core.application.port.output.query.EntryQueryPort
import com.tguimaraes.ledger.core.application.port.output.repository.AccountRepositoryPort
import com.tguimaraes.ledger.core.application.usecase.AccountBalanceUseCase
import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import com.tguimaraes.ledger.core.domain.model.Account
import com.tguimaraes.ledger.core.support.TestFixtures
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant

class AccountBalanceUseCaseTest {
    private val entryQueryPort =  mockk<EntryQueryPort>()
    private val accountRepositoryPort =  mockk<AccountRepositoryPort>()

    private lateinit var useCase: AccountBalanceUseCase

    @BeforeEach
    fun setup() {
        useCase = AccountBalanceUseCase(
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

        Assertions.assertEquals(TestFixtures.FROM_ACCOUNT_ID, result.accountId)
        Assertions.assertEquals(BigDecimal("1500.00"), result.balance)

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

        org.assertj.core.api.Assertions.assertThatThrownBy {
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