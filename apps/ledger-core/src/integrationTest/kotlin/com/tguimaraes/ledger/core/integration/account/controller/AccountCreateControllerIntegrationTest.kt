package com.tguimaraes.ledger.core.integration.account.controller

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.account.CreateAccountRequest
import com.tguimaraes.ledger.core.integration.support.AbstractIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.post

@AutoConfigureMockMvc
class AccountCreateControllerIntegrationTest : AbstractIntegrationTest() {

    @BeforeEach
    fun setup() {
        cleanDatabase()
    }

    @Test
    fun `should throw exception when ownerName is blank`() {
        val request = CreateAccountRequest("")

        mockMvc.performCreate(request)
            .andExpect { status { isBadRequest() } }

        assertThat(accountRepository.count()).isZero()
    }

    @Test
    fun `should create account successfully`() {
        val request = CreateAccountRequest("Thiago")

        mockMvc.performCreate(request)
            .andExpect {
                status { isCreated() }
                jsonPath("$.accountId") { isNotEmpty() }
            }

        assertThat(accountRepository.count()).isEqualTo(1)
    }

    private fun MockMvc.performCreate(
        body: CreateAccountRequest
    ): ResultActionsDsl = post("/accounts") {
        contentType = MediaType.APPLICATION_JSON
        content = objectMapper.writeValueAsString(body)
    }
}