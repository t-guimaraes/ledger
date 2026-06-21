package com.tguimaraes.ledger.core.domain.service

import com.tguimaraes.ledger.core.domain.exception.InvalidAccountDepositAmountException
import com.tguimaraes.ledger.core.domain.model.EntryType
import com.tguimaraes.ledger.core.support.TestFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class AccountDomainServiceTest {
    private val service = AccountDomainService()

    @Test
    fun `should create deposit successfully`() {

        val result = service.createDeposit(
            TestFixtures.FROM_ACCOUNT_ID,
            BigDecimal("100.00"),
        )

        assertEquals(
            BigDecimal("100.00"),
            result.transaction.amount
        )

        assertEquals(
            1,
            result.entries.size
        )

        val credit =
            result.entries.first {
                it.type == EntryType.CREDIT
            }

        assertEquals(
            BigDecimal("100.00"),
            credit.amount
        )

        assertEquals(
            BigDecimal("100.00"),
            credit.amount
        )

        assertEquals(
            result.transaction.id,
            credit.transactionId
        )
    }

    @Test
    fun `should throw when amount is zero`() {

        assertThrows(
            InvalidAccountDepositAmountException::class.java
        ) {
            service.createDeposit(
                TestFixtures.FROM_ACCOUNT_ID,
                BigDecimal.ZERO,
            )
        }
    }
}