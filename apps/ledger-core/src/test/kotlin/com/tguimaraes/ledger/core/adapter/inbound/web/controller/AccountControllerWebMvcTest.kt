package com.tguimaraes.ledger.core.adapter.inbound.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.CreateAccountRequest
import com.tguimaraes.ledger.core.application.dto.AccountStatementResult
import com.tguimaraes.ledger.core.application.dto.CreateAccountCommand
import com.tguimaraes.ledger.core.application.dto.CreateAccountResult
import com.tguimaraes.ledger.core.application.port.input.CreateAccountDepositInputPort
import com.tguimaraes.ledger.core.application.port.input.CreateAccountInputPort
import com.tguimaraes.ledger.core.application.port.input.GetAccountBalanceInputPort
import com.tguimaraes.ledger.core.application.port.input.GetAccountStatementInputPort
import com.tguimaraes.ledger.core.support.TestFixtures
import io.mockk.every
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.math.BigDecimal

@WebMvcTest(AccountController::class)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerWebMvcTest(

    @Autowired
    private val mockMvc: MockMvc,

    @Autowired
    private val objectMapper: ObjectMapper

) {
    @MockkBean
    private lateinit var createAccountInputPort: CreateAccountInputPort

    @MockkBean
    private lateinit var createAccountDepositInputPort: CreateAccountDepositInputPort

    @MockkBean
    private lateinit var getAccountBalanceInputPort: GetAccountBalanceInputPort

    @MockkBean
    private lateinit var getAccountStatementInputPort: GetAccountStatementInputPort




    @Test
    fun `should return account balance`() {

        every {
            getAccountBalanceInputPort.execute(
                TestFixtures.FROM_ACCOUNT_ID
            )
        } returns TestFixtures.accountBalance(BigDecimal("1500.00"))

        mockMvc.get(
            "/accounts/${TestFixtures.FROM_ACCOUNT_ID}/balance"
        ) {
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.accountId") {
                    value(TestFixtures.FROM_ACCOUNT_ID.toString())
                }
                jsonPath("$.balance") {
                    value(1500.00)
                }
            }

        verify(exactly = 1) {
            getAccountBalanceInputPort.execute(
                TestFixtures.FROM_ACCOUNT_ID
            )
        }
    }

    @Test
    fun `should return statement`() {

        every {
            getAccountStatementInputPort.execute(TestFixtures.FROM_ACCOUNT_ID)
        } returns AccountStatementResult(
            accountId = TestFixtures.FROM_ACCOUNT_ID,
            entries = listOf(
                TestFixtures.statementEntryResult()
            )
        )

        mockMvc.get(
            "/accounts/${TestFixtures.FROM_ACCOUNT_ID}/statement"
        ) {
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.accountId") {
                    value(TestFixtures.FROM_ACCOUNT_ID.toString())
                }
                jsonPath("$.entries.length()") {
                    value(1)
                }
                jsonPath("$.entries[0].amount") {
                    value(100.00)
                }
                jsonPath("$.entries[0].type") {
                    value("CREDIT")
                }
            }

        verify(exactly = 1) {
            getAccountStatementInputPort.execute(
                TestFixtures.FROM_ACCOUNT_ID
            )
        }
    }

    @Test
    fun `should create account`() {
        val slot = slot<CreateAccountCommand>()
        val request = CreateAccountRequest("Thiago")

        every {
            createAccountInputPort.execute(capture(slot))
        } returns CreateAccountResult(TestFixtures.FROM_ACCOUNT_ID)

        mockMvc.perform(
            post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.accountId").value(TestFixtures.FROM_ACCOUNT_ID.toString()))


        verify(exactly = 1) {
            createAccountInputPort.execute(any())
        }

        assertEquals("Thiago", slot.captured.ownerName)
    }
}