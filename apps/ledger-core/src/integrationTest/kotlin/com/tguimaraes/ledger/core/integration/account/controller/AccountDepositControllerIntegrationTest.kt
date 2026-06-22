package com.tguimaraes.ledger.core.integration.account.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.account.AccountDepositRequest
import com.tguimaraes.ledger.core.integration.support.AbstractIntegrationTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@AutoConfigureMockMvc
class AccountDepositControllerIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var accountId: UUID
    private lateinit var amount: BigDecimal
    private lateinit var request: AccountDepositRequest

    @BeforeEach
    fun setup() {
        accountId = UUID.randomUUID()
        amount = BigDecimal("1000.00")
        request = AccountDepositRequest(amount)

        cleanEnvironment()
        createAccount(accountId, "Thiago");
    }

    @Test
    fun `should account deposit successfully`() {

        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/accounts/${accountId}/deposit")
                .header(
                    "Idempotency-Key",
                    "integration-key"
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andReturn()
            .response
            .contentAsString

        val json = objectMapper.readTree(response)

        assertEquals(
            accountId,
            UUID.fromString(json.get("accountId").asText())
        )

        Assertions.assertThat(BigDecimal(json.get("amount").asText())).isEqualByComparingTo(amount)

        assertTrue(
            idempotencyRepository.existsById("integration-key")
        )
    }

    @Test
    fun `should return conflict when idempotency key already exists`() {

        createIdempotencyKey("duplicate-key")

        mockMvc.perform(
            MockMvcRequestBuilders.post("/accounts/${accountId}/deposit")
                .header(
                    "Idempotency-Key",
                    "duplicate-key"
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isConflict)

        assertEquals(
            0,
            transactionRepository.count()
        )

        assertEquals(
            0,
            entryRepository.count()
        )
    }

    @Test
    fun `should return bad request when amount is less or equal 0`() {

        val request2 = AccountDepositRequest(BigDecimal.ZERO)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/accounts/${accountId}/deposit")
                .header(
                    "Idempotency-Key",
                    "integration-key"
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(request2)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)

        assertEquals(
            0,
            transactionRepository.count()
        )

        assertEquals(
            0,
            entryRepository.count()
        )
    }

}