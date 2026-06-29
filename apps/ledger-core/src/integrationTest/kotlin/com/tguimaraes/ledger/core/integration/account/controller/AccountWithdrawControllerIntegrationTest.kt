package com.tguimaraes.ledger.core.integration.account.controller

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.account.AccountWithdrawRequest
import com.tguimaraes.ledger.core.integration.support.AbstractIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.post
import java.math.BigDecimal
import java.util.*
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@AutoConfigureMockMvc
class AccountWithdrawControllerIntegrationTest : AbstractIntegrationTest() {

    @BeforeEach
    fun setup() {
        cleanDatabase()
        fromAccountId = UUID.randomUUID().also {
            createAccount(it, "Thiago")
        }
    }

    @Test
    fun `should throw exception when idempotency key already exists`() {
        createIdempotencyKey("duplicate-key")
        val request = AccountWithdrawRequest(BigDecimal("1000.00"))

        mockMvc.performWithdraw(fromAccountId, "duplicate-key", request)
            .andExpect { status { isConflict() } }

        assertThat(idempotencyRepository.count()).isEqualTo(1)
        assertThat(transactionRepository.count()).isZero()
        assertThat(entryRepository.count()).isZero()
    }

    @Test
    fun `should throw exception when account not exists to withdraw`() {
        val accountId = UUID.randomUUID()
        val request = AccountWithdrawRequest(BigDecimal("1000.00"))

        mockMvc.performWithdraw(accountId, "integration-key", request)
            .andExpect {
                status { isNotFound() }
            }

        assertFalse(accountRepository.existsById(accountId))
    }

    @Test
    fun `should throw exception when account balance is insufficient`() {
        val request = AccountWithdrawRequest(BigDecimal("1000.00"))

        mockMvc.performWithdraw(fromAccountId, "integration-key", request)
            .andExpect {
                status { isUnprocessableEntity() }
            }

        assertThat(entryRepository.getBalance(fromAccountId)).isZero()
    }

    @Test
    fun `should throw exception when amount is less or equal 0`() {
        val requestZero = AccountWithdrawRequest(BigDecimal.ZERO)

        mockMvc.performWithdraw(fromAccountId, "integration-key", requestZero)
            .andExpect { status { isBadRequest() } }

        assertThat(idempotencyRepository.count()).isZero()
        assertThat(transactionRepository.count()).isZero()
        assertThat(entryRepository.count()).isZero()
    }

    @Test
    fun `should account withdraw successfully`() {
        fundAccount(fromAccountId, BigDecimal("1000.00"))
        val request = AccountWithdrawRequest(BigDecimal("500.00"))

        mockMvc.performWithdraw(fromAccountId, "integration-key", request)
            .andExpect {
                status { isCreated() }
                jsonPath("$.accountId") { value(fromAccountId.toString()) }
                jsonPath("$.amount") { value(500.00) }
            }

        assertTrue(idempotencyRepository.existsById("integration-key"))
        assertThat(transactionRepository.count()).isEqualTo(2)
        assertThat(entryRepository.count()).isEqualTo(2)
    }

    private fun MockMvc.performWithdraw(
        id: UUID,
        idempotencyKey: String,
        body: AccountWithdrawRequest
    ): ResultActionsDsl = post("/accounts/$id/withdraw") {
        headers { this["Idempotency-Key"] = idempotencyKey }
        contentType = MediaType.APPLICATION_JSON
        content = objectMapper.writeValueAsString(body)
    }
}