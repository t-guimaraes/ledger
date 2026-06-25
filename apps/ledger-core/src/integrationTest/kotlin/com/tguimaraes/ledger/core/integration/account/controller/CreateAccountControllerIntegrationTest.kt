package com.tguimaraes.ledger.core.integration.account.controller

import com.tguimaraes.ledger.core.integration.support.AbstractIntegrationTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@AutoConfigureMockMvc
class CreateAccountControllerIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        cleanEnvironment()
    }

    @Test
    fun `should create account successfully`() {

        val request = mapOf(
            "ownerName" to "Thiago Guimarães"
        )

        val response = mockMvc.post("/accounts") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isCreated() }
            }
            .andReturn()
            .response
            .contentAsString


        val json = objectMapper.readTree(response)
        val accountId = UUID.fromString(json.get("accountId").asText())

        assertNotNull(accountId)

        val savedAccount = accountRepository.findById(accountId).orElseThrow()

        assertEquals("Thiago Guimarães", savedAccount.ownerName)
        assertEquals(accountId, savedAccount.id)
    }

    @Test
    fun `should return bad request when ownerName is blank`() {

        val request = mapOf(
            "ownerName" to ""
        )

        mockMvc.post("/accounts") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isBadRequest() }
            }

        assertEquals(0, accountRepository.count())
    }

    @Test
    fun `should return bad request when ownerName is null`() {

        val request = mapOf<String, String?>(
            "ownerName" to null
        )

        mockMvc.post("/accounts") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isBadRequest() }
            }

        assertEquals(0, accountRepository.count())
    }
}