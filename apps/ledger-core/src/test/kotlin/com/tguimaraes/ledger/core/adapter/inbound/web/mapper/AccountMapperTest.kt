package com.tguimaraes.ledger.core.adapter.inbound.web.mapper

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.CreateAccountRequest
import com.tguimaraes.ledger.core.application.dto.CreateAccountResult
import com.tguimaraes.ledger.core.support.TestFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AccountMapperTest {

    @Test
    fun `should map create account request to command`() {

        val request = CreateAccountRequest(
            ownerName = "Thiago"
        )

        val command = AccountMapper.toCommand(request)

        assertEquals(request.ownerName, command.ownerName)
    }

    @Test
    fun `should map create account result to response`() {

        val result = CreateAccountResult(
            accountId = TestFixtures.FROM_ACCOUNT_ID
        )

        val response = AccountMapper.toResponse(result)

        assertEquals(result.accountId, response.accountId)
    }
}
