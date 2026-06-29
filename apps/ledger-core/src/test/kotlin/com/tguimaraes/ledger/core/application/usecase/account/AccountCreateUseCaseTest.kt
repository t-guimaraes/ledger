package com.tguimaraes.ledger.core.application.usecase.account

import com.tguimaraes.ledger.core.application.dto.account.CreateAccountCommand
import com.tguimaraes.ledger.core.application.port.output.repository.AccountRepositoryPort
import com.tguimaraes.ledger.core.application.usecase.CreateAccountUseCase
import com.tguimaraes.ledger.core.domain.exception.InvalidAccountOwnerNameException
import io.mockk.*
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateAccountUseCaseTest {

    private lateinit var useCase: CreateAccountUseCase

    private val accountRepositoryPort = mockk<AccountRepositoryPort>()

    @BeforeEach
    fun setup() {
        useCase = CreateAccountUseCase(
            accountRepositoryPort
        )
    }

    @Test
    fun `should create account`() {
        every {
            accountRepositoryPort.save(any())
        } just runs

        useCase.execute(CreateAccountCommand(" Thiago "))

        verify(exactly = 1) {
            accountRepositoryPort.save(any())
        }
    }

    @Test
    fun `should throw when owner name is blank`() {
        val command = CreateAccountCommand(
            ownerName = ""
        )

        Assertions.assertThatThrownBy {
            useCase.execute(command)
        }.isInstanceOf(InvalidAccountOwnerNameException::class.java)

        verify(exactly = 0) {
            accountRepositoryPort.save(any())
        }
    }
}