package com.tguimaraes.ledger.core.integration.transfer.controller

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.transfer.TransferRequest
import com.tguimaraes.ledger.core.domain.model.EntryType
import com.tguimaraes.ledger.core.integration.support.AbstractIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.post
import java.math.BigDecimal
import java.util.*
import kotlin.test.assertTrue

@AutoConfigureMockMvc
class TransferControllerIntegrationTest : AbstractIntegrationTest() {

    @BeforeEach
    fun setup() {
        cleanDatabase()
        fromAccountId = UUID.randomUUID().also {
            createAccount(it, "Thiago")
            fundAccount(it, BigDecimal("1000.00"))
        }
        toAccountId = UUID.randomUUID().also {
            createAccount(it, "Maria")
        }
    }

    @Test
    fun `should throw exception when idempotency key already exists`() {
        createIdempotencyKey("duplicate-key")
        val request = TransferRequest(fromAccountId, toAccountId,BigDecimal("100.00"))

        mockMvc.performTransfer("duplicate-key",request)
            .andExpect { status { isConflict() } }

        assertThat(idempotencyRepository.count()).isEqualTo(1)
        assertThat(transactionRepository.count()).isEqualTo(1)
        assertThat(entryRepository.count()).isEqualTo(1)
        assertThat(outboxEventRepository.count()).isZero()
    }

    @Test
    fun `should throw exception when source account does not exist`() {
        val randomAccount = UUID.randomUUID()
        val request = TransferRequest(randomAccount, toAccountId,BigDecimal("100.00"))

        mockMvc.performTransfer("integration-key",request)
            .andExpect { status { isNotFound() } }

        assertFalse(accountRepository.existsById(randomAccount))
    }

    @Test
    fun `should throw exception when destination account does not exist`() {
        val randomAccount = UUID.randomUUID()
        val request = TransferRequest(fromAccountId, randomAccount,BigDecimal("100.00"))

        mockMvc.performTransfer("integration-key",request)
            .andExpect { status { isNotFound() } }

        assertFalse(accountRepository.existsById(randomAccount))
    }

    @Test
    fun `should throw exception when amount is less or equal 0`() {
        val request = TransferRequest(fromAccountId, toAccountId,BigDecimal.ZERO)

        mockMvc.performTransfer("integration-key",request)
            .andExpect { status { isBadRequest() } }
    }

    @Test
    fun `should throw exception when account balance is insufficient`() {
        val request = TransferRequest(fromAccountId, toAccountId,BigDecimal("2000.00"))

        mockMvc.performTransfer("integration-key",request)
            .andExpect { status { isUnprocessableEntity() } }

        assertThat(entryRepository.getBalance(fromAccountId)).isLessThan(BigDecimal("2000.00"))
    }

    @Test
    fun `should create transfer successfully`() {
        val request = TransferRequest(fromAccountId, toAccountId,BigDecimal("200.00"))

        mockMvc.performTransfer("integration-key",request)
            .andExpect {
                status { isCreated() }
            }

        assertTrue(idempotencyRepository.existsById("integration-key"))
        assertThat(transactionRepository.count()).isEqualTo(2)
        assertThat(entryRepository.count()).isEqualTo(3)
        assertThat(outboxEventRepository.count()).isEqualTo(1)

        assertThat(entryRepository.getBalance(fromAccountId)).isEqualTo(BigDecimal("800.00"))
        assertThat(entryRepository.getBalance(toAccountId)).isEqualTo(BigDecimal("200.00"))

        assertThat(entryRepository.findAll())
            .extracting("accountId", "amount", "type")
            .containsExactlyInAnyOrder(
                tuple(fromAccountId, BigDecimal("1000.00"), EntryType.CREDIT),
                tuple(fromAccountId, BigDecimal("200.00"), EntryType.DEBIT),
                tuple(toAccountId, BigDecimal("200.00"), EntryType.CREDIT)
            )
    }

    private fun MockMvc.performTransfer(
        idempotencyKey: String,
        body: TransferRequest
    ): ResultActionsDsl = post("/transfers") {
        headers { this["Idempotency-Key"] = idempotencyKey }
        contentType = MediaType.APPLICATION_JSON
        content = objectMapper.writeValueAsString(body)
    }
}