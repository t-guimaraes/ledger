package com.tguimaraes.ledger.core.domain.service

import com.tguimaraes.ledger.core.domain.exception.InsufficientBalanceException
import com.tguimaraes.ledger.core.domain.exception.InvalidTransferAmountException
import com.tguimaraes.ledger.core.domain.exception.SameAccountTransferException
import com.tguimaraes.ledger.core.domain.model.EntryType
import com.tguimaraes.ledger.core.support.TestFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class TransferDomainServiceTest {
    private val service = TransferDomainService()

    @Test
    fun `should create transfer successfully`() {

        val result = service.createTransfer(
            fromAccount = TestFixtures.fromAccount(),
            toAccount = TestFixtures.toAccount(),
            amount = BigDecimal("100.00"),
            balance = BigDecimal("500.00")
        )

        assertEquals(
            BigDecimal("100.00"),
            result.transaction.amount
        )

        assertEquals(
            2,
            result.entries.size
        )

        val debit =
            result.entries.first {
                it.type == EntryType.DEBIT
            }

        val credit =
            result.entries.first {
                it.type == EntryType.CREDIT
            }

        assertEquals(
            TestFixtures.FROM_ACCOUNT_ID,
            debit.accountId
        )

        assertEquals(
            TestFixtures.TO_ACCOUNT_ID,
            credit.accountId
        )

        assertEquals(
            BigDecimal("100.00"),
            debit.amount
        )

        assertEquals(
            BigDecimal("100.00"),
            credit.amount
        )

        assertEquals(
            result.transaction.id,
            debit.transactionId
        )

        assertEquals(
            result.transaction.id,
            credit.transactionId
        )
    }

    @Test
    fun `should throw when balance is insufficient`() {

        assertThrows(
            InsufficientBalanceException::class.java
        ) {
            service.createTransfer(
                fromAccount = TestFixtures.fromAccount(),
                toAccount = TestFixtures.toAccount(),
                amount = BigDecimal("100.00"),
                balance = BigDecimal("50.00")
            )
        }
    }

    @Test
    fun `should throw when accounts are the same`() {

        val account = TestFixtures.fromAccount()

        assertThrows(
            SameAccountTransferException::class.java
        ) {
            service.createTransfer(
                fromAccount = account,
                toAccount = account,
                amount = BigDecimal("100.00"),
                balance = BigDecimal("500.00")
            )
        }
    }

    @Test
    fun `should throw when amount is zero`() {

        assertThrows(
            InvalidTransferAmountException::class.java
        ) {
            service.createTransfer(
                fromAccount = TestFixtures.fromAccount(),
                toAccount = TestFixtures.toAccount(),
                amount = BigDecimal.ZERO,
                balance = BigDecimal("500.00")
            )
        }
    }

    @Test
    fun `should throw when amount is negative`() {

        assertThrows(
            InvalidTransferAmountException::class.java
        ) {
            service.createTransfer(
                fromAccount = TestFixtures.fromAccount(),
                toAccount = TestFixtures.toAccount(),
                amount = BigDecimal("-1.00"),
                balance = BigDecimal("500.00")
            )
        }
    }
}