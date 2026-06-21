package com.tguimaraes.ledger.core.domain.service

import com.tguimaraes.ledger.core.domain.exception.InvalidAccountDepositAmountException
import com.tguimaraes.ledger.core.domain.exception.InvalidAccountWithdrawAmountException
import com.tguimaraes.ledger.core.domain.model.EntryType
import com.tguimaraes.ledger.core.support.TestFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class AccountDomainServiceTest {
    private val service = AccountDomainService()

    @Test
    fun `should throw when deposit amount is zero`() {

        assertThrows(
            InvalidAccountDepositAmountException::class.java
        ) {
            service.deposit(
                TestFixtures.FROM_ACCOUNT_ID,
                BigDecimal.ZERO,
            )
        }
    }

    @Test
    fun `should create deposit successfully`() {

        val result = service.deposit(
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
    fun `should throw when withdraw amount is zero`() {

        assertThrows(
            InvalidAccountWithdrawAmountException::class.java
        ) {
            service.withdraw(
                TestFixtures.FROM_ACCOUNT_ID,
                BigDecimal.ZERO,
            )
        }
    }

    @Test
    fun `should create withdraw successfully`() {

        val result = service.withdraw(
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

        val debit =
            result.entries.first {
                it.type == EntryType.DEBIT
            }

        assertEquals(
            BigDecimal("100.00"),
            debit.amount
        )

        assertEquals(
            BigDecimal("100.00"),
            debit.amount
        )

        assertEquals(
            result.transaction.id,
            debit.transactionId
        )
    }
}