package com.tguimaraes.ledger.core.adapter.inbound.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.account.AccountDepositRequest
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.account.AccountWithdrawRequest
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.account.CreateAccountRequest
import com.tguimaraes.ledger.core.application.dto.account.*
import com.tguimaraes.ledger.core.application.port.input.*
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
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
    private lateinit var accountDepositInputPort: AccountDepositInputPort

    @MockkBean
    private lateinit var accountWithdrawInputPort: AccountWithdrawInputPort

    @MockkBean
    private lateinit var accountBalanceInputPort: AccountBalanceInputPort

    @MockkBean
    private lateinit var accountStatementInputPort: AccountStatementInputPort


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

    @Test
    fun `should deposit an account`() {
        val amount = BigDecimal("1500.00")
        val request = AccountDepositRequest(amount)
        val result = AccountDepositResult(TestFixtures.FROM_ACCOUNT_ID, amount)

        every {
            accountDepositInputPort.deposit(
                AccountDepositCommand(amount),
                TestFixtures.FROM_ACCOUNT_ID,
                TestFixtures.IDEMPOTENCY_KEY
            )
        } returns result

        mockMvc.perform(
            post("/accounts/${TestFixtures.FROM_ACCOUNT_ID}/deposit")
                .header(
                    "Idempotency-Key",
                    TestFixtures.IDEMPOTENCY_KEY
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.accountId").value(TestFixtures.FROM_ACCOUNT_ID.toString()))
            .andExpect(jsonPath("$.amount").value(amount.toDouble()))


        verify(exactly = 1) {
            accountDepositInputPort.deposit(any(), any(), any())
        }
    }

    @Test
    fun `should withdraw an account`() {
        val amount = BigDecimal("1500.00")
        val request = AccountWithdrawRequest(amount)
        val result = AccountWithdrawResult(TestFixtures.FROM_ACCOUNT_ID, amount)

        every {
            accountWithdrawInputPort.withdraw(
                AccountWithdrawCommand(amount),
                TestFixtures.FROM_ACCOUNT_ID,
                TestFixtures.IDEMPOTENCY_KEY
            )
        } returns result

        mockMvc.perform(
            post("/accounts/${TestFixtures.FROM_ACCOUNT_ID}/withdraw")
                .header(
                    "Idempotency-Key",
                    TestFixtures.IDEMPOTENCY_KEY
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.accountId").value(TestFixtures.FROM_ACCOUNT_ID.toString()))
            .andExpect(jsonPath("$.amount").value(amount.toDouble()))


        verify(exactly = 1) {
            accountWithdrawInputPort.withdraw(any(), any(), any())
        }
    }

    @Test
    fun `should return account balance`() {

        every {
            accountBalanceInputPort.execute(
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
            accountBalanceInputPort.execute(
                TestFixtures.FROM_ACCOUNT_ID
            )
        }
    }

    @Test
    fun `should return statement`() {

        every {
            accountStatementInputPort.execute(TestFixtures.FROM_ACCOUNT_ID)
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
            accountStatementInputPort.execute(
                TestFixtures.FROM_ACCOUNT_ID
            )
        }
    }
}