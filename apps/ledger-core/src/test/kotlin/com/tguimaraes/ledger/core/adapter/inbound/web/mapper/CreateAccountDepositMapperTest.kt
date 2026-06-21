package com.tguimaraes.ledger.core.adapter.inbound.web.mapper

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.CreateAccountDepositRequest
import com.tguimaraes.ledger.core.application.dto.CreateAccountDepositResult
import com.tguimaraes.ledger.core.support.TestFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class CreateAccountDepositMapperTest {

    @Test
    fun `should map create account deposit request to command`() {

        val request = CreateAccountDepositRequest(
            amount = BigDecimal("1000.00")
        )

        val command = CreateAccountDepositMapper.toCommand(request)

        assertEquals(request.amount, command.amount)
    }

    @Test
    fun `should map create account deposit result to response`() {

        val result = CreateAccountDepositResult(
            TestFixtures.FROM_ACCOUNT_ID,
            amount = BigDecimal("1000.00")
        )

        val response = CreateAccountDepositMapper.toResponse(result)

        assertEquals(result.accountId, response.accountId)
        assertEquals(result.amount, response.amount)
    }
}