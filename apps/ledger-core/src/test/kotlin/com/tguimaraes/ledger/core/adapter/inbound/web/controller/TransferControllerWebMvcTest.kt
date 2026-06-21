package com.tguimaraes.ledger.core.adapter.inbound.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.tguimaraes.ledger.core.adapter.inbound.web.exception.GlobalExceptionHandler
import com.tguimaraes.ledger.core.application.port.input.TransferInputPort
import com.tguimaraes.ledger.core.support.TestFixtures
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal

@WebMvcTest(TransferController::class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler::class)
class TransferControllerWebMvcTest(

    @Autowired
    private val mockMvc: MockMvc,

    @Autowired
    private val objectMapper: ObjectMapper

) {
    @MockkBean
    private lateinit var transferInputPort: TransferInputPort

    @Test
    fun `should return bad request when amount is below minimum`() {

        val request = TestFixtures.createTransferRequest(
            amount = BigDecimal.ZERO
        )

        mockMvc.perform(
            post("/transfers")
                .header(
                    "Idempotency-Key",
                    TestFixtures.IDEMPOTENCY_KEY
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
            .andExpect(status().isBadRequest)

        verify(exactly = 0) {
            transferInputPort.transfer(any(), any())
        }
    }

    @Test
    fun `should return bad request when idempotency key is missing`() {

        val request = TestFixtures.createTransferRequest()

        mockMvc.perform(
            post("/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
            .andExpect(status().isBadRequest)

        verify(exactly = 0) {
            transferInputPort.transfer(any(), any())
        }
    }

    @Test
    fun `should call input port when request is valid and transfer succeeds`() {

        val request = TestFixtures.createTransferRequest()

        every {
            transferInputPort.transfer(
                TestFixtures.createTransferCommand(),
                TestFixtures.IDEMPOTENCY_KEY
            )
        } just runs

        mockMvc.perform(
            post("/transfers")
                .header(
                    "Idempotency-Key",
                    TestFixtures.IDEMPOTENCY_KEY
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
            .andExpect(status().isCreated)

        verify(exactly = 1) {
            transferInputPort.transfer(
                TestFixtures.createTransferCommand(),
                TestFixtures.IDEMPOTENCY_KEY
            )
        }
    }
}
