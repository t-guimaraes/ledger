package com.tguimaraes.ledger.core.adapter.inbound.web.mapper.account

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.account.AccountDepositRequest
import com.tguimaraes.ledger.core.application.dto.account.AccountDepositResult
import com.tguimaraes.ledger.core.support.TestFixtures
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class AccountDepositMapperTest {

    @Test
    fun `should map create account deposit request to command`() {

        val request = AccountDepositRequest(
            amount = BigDecimal("1000.00")
        )

        val command = AccountDepositMapper.toCommand(request)

        Assertions.assertEquals(request.amount, command.amount)
    }

    @Test
    fun `should map create account deposit result to response`() {

        val result = AccountDepositResult(
            TestFixtures.FROM_ACCOUNT_ID,
            amount = BigDecimal("1000.00")
        )

        val response = AccountDepositMapper.toResponse(result)

        Assertions.assertEquals(result.accountId, response.accountId)
        Assertions.assertEquals(result.amount, response.amount)
    }
}