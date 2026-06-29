package com.tguimaraes.ledger.core.integration.account.controller

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.account.AccountDepositRequest
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
class AccountDepositControllerIntegrationTest : AbstractIntegrationTest() {

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
        val request = AccountDepositRequest(BigDecimal("1000.00"))

        mockMvc.performDeposit(fromAccountId, "duplicate-key", request)
            .andExpect { status { isConflict() } }

        assertThat(idempotencyRepository.count()).isEqualTo(1)
        assertThat(transactionRepository.count()).isZero()
        assertThat(entryRepository.count()).isZero()
    }

    @Test
    fun `sshould throw exception when account not exists to deposit`() {
        val accountId = UUID.randomUUID()
        val request = AccountDepositRequest(BigDecimal("1000.00"))

        mockMvc.performDeposit(accountId, "integration-key", request)
            .andExpect {
                status { isNotFound() }
            }

        assertFalse(accountRepository.existsById(accountId))
    }

    @Test
    fun `should throw exception when amount is less or equal 0`() {
        val requestZero = AccountDepositRequest(BigDecimal.ZERO)

        mockMvc.performDeposit(fromAccountId, "integration-key", requestZero)
            .andExpect { status { isBadRequest() } }

        assertThat(idempotencyRepository.count()).isZero()
        assertThat(transactionRepository.count()).isZero()
        assertThat(entryRepository.count()).isZero()
    }

    @Test
    fun `should account deposit successfully`() {
        val request = AccountDepositRequest(BigDecimal("1000.00"))

        mockMvc.performDeposit(fromAccountId, "integration-key", request)
            .andExpect {
                status { isCreated() }
                jsonPath("$.accountId") { value(fromAccountId.toString()) }
                jsonPath("$.amount") { value(1000.00) }
            }

        assertTrue(idempotencyRepository.existsById("integration-key"))
        assertThat(transactionRepository.count()).isEqualTo(1)
        assertThat(entryRepository.count()).isEqualTo(1)
    }

    private fun MockMvc.performDeposit(
        id: UUID,
        idempotencyKey: String,
        body: AccountDepositRequest
    ): ResultActionsDsl = post("/accounts/$id/deposit") {
        headers { this["Idempotency-Key"] = idempotencyKey }
        contentType = MediaType.APPLICATION_JSON
        content = objectMapper.writeValueAsString(body)
    }
}