package com.tguimaraes.ledger.core.integration.account

import com.tguimaraes.ledger.core.application.dto.CreateTransferCommand
import com.tguimaraes.ledger.core.application.usecase.CreateTransferUseCase
import com.tguimaraes.ledger.core.integration.support.AbstractIntegrationTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.math.BigDecimal
import java.util.UUID

@AutoConfigureMockMvc
class AccountControllerIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var createTransferUseCase: CreateTransferUseCase

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

        mockMvc.get(
            "/accounts/$fromAccountId/balance"
        ) {
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }

                jsonPath("$.accountId") {
                    value(fromAccountId.toString())
                }

                jsonPath("$.balance") {
                    value(1000.00)
                }
            }
    }

    @Test
    fun `should return zero balance when account has no entries`() {

        mockMvc.get(
            "/accounts/$fromAccountId/balance"
        ) {
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }

                jsonPath("$.accountId") {
                    value(fromAccountId.toString())
                }

                jsonPath("$.balance") {
                    value(0)
                }
            }
    }

    @Test
    fun `should calculate balance with credits and debits`() {

        fundAccount(
            accountId = fromAccountId,
            amount = BigDecimal("1000.00")
        )

        val secondAccountId = UUID.randomUUID()

        createAccount(
            id = secondAccountId,
            ownerName = "Maria"
        )

        createTransferUseCase.transfer(
            CreateTransferCommand(
                fromAccountId = fromAccountId,
                toAccountId = secondAccountId,
                amount = BigDecimal("300.00")
            ),
            "balance-test-key"
        )

        mockMvc.get(
            "/accounts/$fromAccountId/balance"
        ) {
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }

                jsonPath("$.accountId") {
                    value(fromAccountId.toString())
                }

                jsonPath("$.balance") {
                    value(700.00)
                }
            }
    }
}