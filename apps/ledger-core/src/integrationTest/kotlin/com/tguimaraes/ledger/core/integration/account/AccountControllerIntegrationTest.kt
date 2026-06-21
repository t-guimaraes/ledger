package com.tguimaraes.ledger.core.integration.account

import com.tguimaraes.ledger.core.integration.support.AbstractIntegrationTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.math.BigDecimal
import java.util.*

@AutoConfigureMockMvc
class AccountControllerIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {

        cleanEnvironment()

        fromAccountId = UUID.randomUUID()

        createAccount(
            id = fromAccountId,
            ownerName = "Thiago"
        )
    }

    @Test
    fun `should return account balance`() {

        fundAccount(
            accountId = fromAccountId,
            amount = BigDecimal("1000.00")
        )

        mockMvc.get("/accounts/$fromAccountId/balance")
            { contentType = MediaType.APPLICATION_JSON }
            .andExpect {
                status { isOk() }
                jsonPath("$.accountId") { value(fromAccountId.toString())}
                jsonPath("$.balance") {value(1000.00)}
            }
    }

    @Test
    fun `should return account statement`() {

        fundAccount(
            fromAccountId,
            BigDecimal("1000.00")
        )

        fundAccount(
            fromAccountId,
            BigDecimal("500.00")
        )

        mockMvc.get("/accounts/$fromAccountId/statement")
            { contentType = MediaType.APPLICATION_JSON }
            .andExpect {
                status { isOk() }
                jsonPath("$.accountId") { value(fromAccountId.toString()) }
                jsonPath("$.entries.length()") { value(2) }
                jsonPath("$.entries[0].amount") { value(500.00) }
                jsonPath("$.entries[1].amount") { value(1000.00) }
                jsonPath("$.entries[0].type") { value("CREDIT") }
                jsonPath("$.entries[1].type") { value("CREDIT") }
            }
    }


}