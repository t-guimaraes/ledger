package com.tguimaraes.ledger.core.integration.transfer

import com.fasterxml.jackson.databind.ObjectMapper
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.CreateTransferRequest
import com.tguimaraes.ledger.core.domain.model.EntryType
import com.tguimaraes.ledger.core.integration.AbstractIntegrationTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@AutoConfigureMockMvc
class TransferControllerIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {

        cleanEnvironment()

        fromAccountId = UUID.randomUUID()
        toAccountId = UUID.randomUUID()

        createAccount(
            fromAccountId,
            "Thiago"
        )

        createAccount(
            toAccountId,
            "Maria"
        )

        fundAccount(
            accountId = fromAccountId,
            amount = BigDecimal("1000.00")
        )
    }

    @Test
    fun `should create transfer successfully`() {

        val request =
            CreateTransferRequest(
                fromAccountId = fromAccountId,
                toAccountId = toAccountId,
                amount = BigDecimal("200.00")
            )

        mockMvc.perform(
            post("/transfers")
                .header(
                    "Idempotency-Key",
                    "integration-key"
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
            .andExpect(status().isCreated)

        val entries = entryRepository.findAll()

        assertEquals(
            2,
            entries.count { it.type == EntryType.CREDIT }
        )

        assertEquals(
            1,
            entries.count { it.type == EntryType.DEBIT }
        )

        assertEquals(
            2,
            transactionRepository.count()
        )

        assertEquals(
            3,
            entryRepository.count()
        )

        assertEquals(
            BigDecimal("800.00"),
            entryRepository.getBalance(fromAccountId)
        )

        assertEquals(
            BigDecimal("200.00"),
            entryRepository.getBalance(toAccountId)
        )

        assertTrue(
            redisTemplate.hasKey("integration-key")
        )
    }

    @Test
    fun `should return conflict when idempotency key already exists`() {

        redisTemplate.opsForValue().set(
            "duplicate-key",
            "processed"
        )

        val request =
            CreateTransferRequest(
                fromAccountId = fromAccountId,
                toAccountId = toAccountId,
                amount = BigDecimal("100.00")
            )

        mockMvc.perform(
            post("/transfers")
                .header(
                    "Idempotency-Key",
                    "duplicate-key"
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
            .andExpect(status().isConflict)

        assertEquals(
            1,
            transactionRepository.count()
        )

        assertEquals(
            1,
            entryRepository.count()
        )
    }

    @Test
    fun `should return unprocessable entity when balance is insufficient`() {

        val request =
            CreateTransferRequest(
                fromAccountId = fromAccountId,
                toAccountId = toAccountId,
                amount = BigDecimal("2000.00")
            )

        mockMvc.perform(
            post("/transfers")
                .header(
                    "Idempotency-Key",
                    "insufficient-balance"
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
            .andExpect(
                status().isUnprocessableEntity
            )

        assertEquals(
            BigDecimal("1000.00"),
            entryRepository.getBalance(fromAccountId)
        )

        assertEquals(
            BigDecimal.ZERO,
            entryRepository.getBalance(toAccountId)
        )
    }

    @Test
    fun `should return not found when source account does not exist`() {

        val request =
            CreateTransferRequest(
                fromAccountId = UUID.randomUUID(),
                toAccountId = toAccountId,
                amount = BigDecimal("100.00")
            )

        mockMvc.perform(
            post("/transfers")
                .header(
                    "Idempotency-Key",
                    "not-found"
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should return bad request when amount is invalid`() {

        val request =
            CreateTransferRequest(
                fromAccountId = fromAccountId,
                toAccountId = toAccountId,
                amount = BigDecimal("0.001")
            )

        mockMvc.perform(
            post("/transfers")
                .header(
                    "Idempotency-Key",
                    "bad-request"
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
            .andExpect(status().isBadRequest)
    }
}