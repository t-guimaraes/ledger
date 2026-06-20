package com.tguimaraes.ledger.core.application.usecase

import com.tguimaraes.ledger.core.application.dto.CreateAccountCommand
import com.tguimaraes.ledger.core.application.port.output.id.IdGeneratorPort
import com.tguimaraes.ledger.core.application.port.output.repository.AccountRepositoryPort
import com.tguimaraes.ledger.core.domain.exception.InvalidAccountOwnerNameException
import com.tguimaraes.ledger.core.domain.model.Account
import com.tguimaraes.ledger.core.support.TestFixtures
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.ZoneOffset

class CreateAccountUseCaseTest {

    private lateinit var useCase: CreateAccountUseCase

    private val accountRepositoryPort = mockk<AccountRepositoryPort>()
    private val idGeneratorPort = mockk<IdGeneratorPort>()
    private val clock = Clock.fixed(TestFixtures.CREATED_AT, ZoneOffset.UTC)

    @BeforeEach
    fun setup() {
        useCase = CreateAccountUseCase(
            accountRepositoryPort,
            idGeneratorPort,
            clock
        )
    }

    @Test
    fun `should create account`() {
        val accountSlot = slot<Account>()

        every {
            accountRepositoryPort.save(capture(accountSlot))
        } just runs
        every {
            idGeneratorPort.generate()
        } returns TestFixtures.FROM_ACCOUNT_ID

        val command = CreateAccountCommand(
            ownerName = " Thiago "
        )

        val result = useCase.execute(command)

        verify(exactly = 1) {
            accountRepositoryPort.save(any())
        }

        assertThat(result.accountId)
            .isEqualTo(TestFixtures.FROM_ACCOUNT_ID)

        assertThat(accountSlot.captured.id)
            .isEqualTo(TestFixtures.FROM_ACCOUNT_ID)

        assertThat(accountSlot.captured.ownerName)
            .isEqualTo("Thiago")

        assertThat(accountSlot.captured.createdAt)
            .isEqualTo(TestFixtures.CREATED_AT)

        assertThat(accountSlot.captured.updatedAt)
            .isNull()
    }

    @Test
    fun `should throw when owner name is blank`() {
        val command = CreateAccountCommand(
            ownerName = ""
        )

        assertThatThrownBy {
            useCase.execute(command)
        }.isInstanceOf(InvalidAccountOwnerNameException::class.java)

        verify(exactly = 0) {
            accountRepositoryPort.save(any())
        }
    }
}
