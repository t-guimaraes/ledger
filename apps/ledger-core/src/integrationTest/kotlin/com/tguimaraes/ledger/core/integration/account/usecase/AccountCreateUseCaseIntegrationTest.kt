package com.tguimaraes.ledger.core.integration.account.usecase

import com.tguimaraes.ledger.core.application.dto.account.CreateAccountCommand
import com.tguimaraes.ledger.core.application.port.input.CreateAccountInputPort
import com.tguimaraes.ledger.core.domain.exception.InvalidAccountOwnerNameException
import com.tguimaraes.ledger.core.integration.support.AbstractIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class AccountCreateUseCaseIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var createAccountInputPort: CreateAccountInputPort

    @BeforeEach
    fun setup() {
        cleanDatabase()
    }

    @Test
    fun `should throw exception when owner name is blank`() {
        val command = CreateAccountCommand("   ")

        assertThrows(InvalidAccountOwnerNameException::class.java) {
            createAccountInputPort.execute(command)
        }
        assertThat(accountRepository.count()).isZero()
    }

    @Test
    fun `should create account successfully`() {
        val command = CreateAccountCommand("   Thiago   ")

        val result = createAccountInputPort.execute(command)
        assertNotNull(result.accountId)

        val account = accountRepository.findById(result.accountId).get()
        assertThat(account.id).isEqualTo(result.accountId)
        assertThat(account.ownerName).isEqualTo("Thiago")
    }
}