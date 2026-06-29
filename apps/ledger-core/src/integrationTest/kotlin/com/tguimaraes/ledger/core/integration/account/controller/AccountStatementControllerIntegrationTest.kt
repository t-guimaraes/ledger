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
class AccountStatementControllerIntegrationTest : AbstractIntegrationTest() {

    @BeforeEach
    fun setup() {
        cleanDatabase()
        fromAccountId = UUID.randomUUID().also {
            createAccount(it, "Thiago")
        }
    }

    @Test
    fun `should throw exception when account not exists to get statement`() {
        val accountId = UUID.randomUUID()
        mockMvc.performStatement(accountId)
            .andExpect {
                status { isNotFound() }
            }

        assertFalse(accountRepository.existsById(accountId))
    }

    @Test
    fun `should return account statement`() {
        fundAccount(fromAccountId,BigDecimal("1000.00"))
        fundAccount(fromAccountId,BigDecimal("500.00"))

        mockMvc.performStatement()
            .andExpect {
                status { isOk() }
                jsonPath("$.accountId") { value(fromAccountId.toString()) }
                jsonPath("$.entries.length()") { value(2) }
                jsonPath("$.entries[0].amount") { value(500.00) }
                jsonPath("$.entries[1].amount") { value(1000.00) }
                jsonPath("$.entries[0].type") { value("CREDIT") }
                jsonPath("$.entries[1].type") { value("CREDIT") }
            }

        assertThat(transactionRepository.count()).isEqualTo(2)
        assertThat(entryRepository.count()).isEqualTo(2)
    }

    private fun MockMvc.performStatement(
        accountId: UUID = fromAccountId
    ): ResultActionsDsl = get("/accounts/$accountId/statement") {
        contentType = MediaType.APPLICATION_JSON
    }
}