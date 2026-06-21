package com.tguimaraes.ledger.core.adapter.inbound.web.mapper.account

import com.tguimaraes.ledger.core.application.dto.account.AccountBalanceResult
import com.tguimaraes.ledger.core.support.TestFixtures
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class AccountBalanceMapperTest {
    @Test
    fun `should map account balance result to balance response`() {

        val result = AccountBalanceResult(
            accountId = TestFixtures.FROM_ACCOUNT_ID,
            balance = BigDecimal("1500.00")
        )

        val response = AccountBalanceMapper.toResponse(result)

        Assertions.assertEquals(result.accountId, response.accountId)
        Assertions.assertEquals(result.balance, response.balance)
    }
}