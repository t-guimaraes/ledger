package com.tguimaraes.ledger.core.domain.service

import com.tguimaraes.ledger.core.domain.dto.TransferResult
import com.tguimaraes.ledger.core.domain.exception.InsufficientBalanceException
import com.tguimaraes.ledger.core.domain.exception.InvalidTransferAmountException
import com.tguimaraes.ledger.core.domain.exception.SameAccountTransferException
import com.tguimaraes.ledger.core.domain.model.Account
import com.tguimaraes.ledger.core.domain.model.Entry
import com.tguimaraes.ledger.core.domain.model.EntryType
import com.tguimaraes.ledger.core.domain.model.Transaction
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class TransferDomainService {
    fun createTransfer(
        fromAccount: Account,
        toAccount: Account,
        amount: BigDecimal,
        balance: BigDecimal,
    ): TransferResult {

        validateAmount(amount)
        validateAccounts(
            fromAccount,
            toAccount
        )
        validateBalance(
            balance,
            amount
        )

        val transaction = Transaction(
            id = UUID.randomUUID(),
            amount = amount,
            createdAt = Instant.now()
        )

        val entries = listOf(
            Entry(
                id = UUID.randomUUID(),
                transactionId = transaction.id,
                accountId = fromAccount.id,
                type = EntryType.DEBIT,
                amount = amount,
                createdAt = Instant.now()
            ),

            Entry(
                id = UUID.randomUUID(),
                transactionId = transaction.id,
                accountId = toAccount.id,
                type = EntryType.CREDIT,
                amount = amount,
                createdAt = Instant.now()
            )
        )

        return TransferResult(
            transaction = transaction,
            entries = entries
        )
    }

    private fun validateAmount(
        amount: BigDecimal
    ) {
        if (amount <= BigDecimal.ZERO) {
            throw InvalidTransferAmountException()
        }
    }

    private fun validateAccounts(
        fromAccount: Account,
        toAccount: Account
    ) {
        if (fromAccount.id == toAccount.id) {
            throw SameAccountTransferException()
        }
    }

    private fun validateBalance(
        balance: BigDecimal,
        amount: BigDecimal
    ) {
        if (balance < amount) {
            throw InsufficientBalanceException()
        }
    }
}