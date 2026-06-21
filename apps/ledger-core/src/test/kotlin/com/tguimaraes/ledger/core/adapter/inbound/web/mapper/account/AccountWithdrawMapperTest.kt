package com.tguimaraes.ledger.core.adapter.inbound.web.mapper.account

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.account.AccountWithdrawRequest
import com.tguimaraes.ledger.core.application.dto.account.AccountWithdrawResult
import com.tguimaraes.ledger.core.support.TestFixtures
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class AccountWithdrawMapperTest {

    @Test
    fun `should map create account withdraw request to command`() {

        val request = AccountWithdrawRequest(
            amount = BigDecimal("1000.00")
        )

        val command = AccountWithdrawMapper.toCommand(request)

        Assertions.assertEquals(request.amount, command.amount)
    }

    @Test
    fun `should map create account withdraw result to response`() {

        val result = AccountWithdrawResult(
            TestFixtures.FROM_ACCOUNT_ID,
            amount = BigDecimal("1000.00")
        )

        val response = AccountWithdrawMapper.toResponse(result)

        Assertions.assertEquals(result.accountId, response.accountId)
        Assertions.assertEquals(result.amount, response.amount)
    }
}