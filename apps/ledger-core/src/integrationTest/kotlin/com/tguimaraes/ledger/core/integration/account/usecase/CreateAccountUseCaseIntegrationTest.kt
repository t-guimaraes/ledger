package com.tguimaraes.ledger.core.integration.account.usecase

import com.tguimaraes.ledger.core.application.dto.account.CreateAccountCommand
import com.tguimaraes.ledger.core.application.port.input.CreateAccountInputPort
import com.tguimaraes.ledger.core.domain.exception.InvalidAccountOwnerNameException
import com.tguimaraes.ledger.core.integration.support.AbstractIntegrationTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CreateAccountUseCaseIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var createAccountInputPort: CreateAccountInputPort

    @BeforeEach
    fun setup() {
        cleanEnvironment()
    }

    @Test
    fun `should throw when owner name is blank`() {

        val command = CreateAccountCommand(
            ownerName = "   "
        )

        assertThrows(InvalidAccountOwnerNameException::class.java) {
            createAccountInputPort.execute(command)
        }

        assertEquals(
            0,
            accountRepository.count()
        )
    }

    @Test
    fun `should create account successfully`() {

        val command = CreateAccountCommand(
            ownerName = "   Thiago Henrique   "
        )

        val result = createAccountInputPort.execute(command)

        assertNotNull(result.accountId)

        val account = accountRepository.findById(result.accountId)

        assertTrue(account.isPresent)

        assertEquals(
            result.accountId,
            account.get().id
        )

        assertEquals(
            "Thiago Henrique",
            account.get().ownerName
        )
    }
}