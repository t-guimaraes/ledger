package com.tguimaraes.ledger.core.adapter.inbound.web.mapper

import com.tguimaraes.ledger.core.application.dto.AccountBalanceResult
import com.tguimaraes.ledger.core.support.TestFixtures
import org.junit.jupiter.api.Assertions.assertEquals
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

        assertEquals(result.accountId, response.accountId)
        assertEquals(result.balance, response.balance)
    }
}