package com.tguimaraes.ledger.core.integration.account.controller

import com.tguimaraes.ledger.core.integration.support.AbstractIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.get
import java.math.BigDecimal
import java.util.*
import kotlin.test.assertFalse

@AutoConfigureMockMvc
class AccountBalanceControllerIntegrationTest : AbstractIntegrationTest() {

    @BeforeEach
    fun setup() {
        cleanDatabase()
        fromAccountId = UUID.randomUUID().also {
            createAccount(it, "Thiago")
        }
    }

    @Test
    fun `should throw exception when account not exists to get balance`() {
        val accountId = UUID.randomUUID()
        mockMvc.performBalance(accountId)
            .andExpect {
                status { isNotFound() }
            }

        assertFalse(accountRepository.existsById(accountId))
    }

    @Test
    fun `should return account balance`() {
        fundAccount(fromAccountId,BigDecimal("1000.00"))

        mockMvc.performBalance()
            .andExpect {
                status { isOk() }
                jsonPath("$.accountId") { value(fromAccountId.toString())}
                jsonPath("$.balance") {value(1000.00)}
            }

        assertThat(transactionRepository.count()).isEqualTo(1)
        assertThat(entryRepository.count()).isEqualTo(1)
    }

    private fun MockMvc.performBalance(
        accountId: UUID = fromAccountId
    ): ResultActionsDsl = get("/accounts/$accountId/balance") {
        contentType = MediaType.APPLICATION_JSON
    }
}