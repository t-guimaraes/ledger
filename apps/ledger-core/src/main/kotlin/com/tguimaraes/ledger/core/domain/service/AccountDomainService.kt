package com.tguimaraes.ledger.core.domain.service

import com.tguimaraes.ledger.core.domain.dto.DepositResult
import com.tguimaraes.ledger.core.domain.dto.WithdrawResult
import com.tguimaraes.ledger.core.domain.exception.InsufficientBalanceException
import com.tguimaraes.ledger.core.domain.exception.InvalidAccountDepositAmountException
import com.tguimaraes.ledger.core.domain.exception.InvalidAccountWithdrawAmountException
import com.tguimaraes.ledger.core.domain.model.Entry
import com.tguimaraes.ledger.core.domain.model.EntryType
import com.tguimaraes.ledger.core.domain.model.Transaction
import java.math.BigDecimal
import java.time.Instant
import java.util.*

class AccountDomainService {
    fun deposit( accountId: UUID,amount: BigDecimal): DepositResult {
        validateAmount(amount, EntryType.CREDIT)

        val transaction = createTransaction(amount)
        val entry = createEntry(transaction, accountId, amount, EntryType.CREDIT)

        return DepositResult(
            transaction = transaction,
            entries = listOf(entry)
        )
    }

    fun withdraw(accountId: UUID, amount: BigDecimal, balance: BigDecimal): WithdrawResult {
        validateAmount(amount, EntryType.DEBIT)
        validateBalance(balance, amount)

        val transaction = createTransaction(amount)
        val entry = createEntry(transaction, accountId, amount, EntryType.DEBIT)

        return WithdrawResult(
            transaction = transaction,
            entries = listOf(entry)
        )
    }

    private fun validateAmount(
        amount: BigDecimal,
        type: EntryType
    ) {
        if (amount <= BigDecimal.ZERO) {
            if(type == EntryType.DEBIT) {
                throw InvalidAccountWithdrawAmountException()
            } else {
                throw InvalidAccountDepositAmountException()
            }
        }
    }

    private fun createTransaction(amount: BigDecimal): Transaction {
        return Transaction(
            id = UUID.randomUUID(),
            amount = amount,
            createdAt = Instant.now()
        )
    }

    private fun createEntry(transaction: Transaction, accountId: UUID, amount: BigDecimal, entryType: EntryType): Entry {
        return Entry(
            id = UUID.randomUUID(),
            transactionId = transaction.id,
            accountId = accountId,
            type = entryType,
            amount = amount,
            createdAt = Instant.now()
        )
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