package com.tguimaraes.ledger.core.domain.service

import com.tguimaraes.ledger.core.domain.dto.DepositResult
import com.tguimaraes.ledger.core.domain.exception.InvalidAccountDepositAmountException
import com.tguimaraes.ledger.core.domain.model.Entry
import com.tguimaraes.ledger.core.domain.model.EntryType
import com.tguimaraes.ledger.core.domain.model.Transaction
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class AccountDomainService {
    fun createDeposit(
        accountId: UUID,
        amount: BigDecimal,
    ): DepositResult {

        validateAmount(amount)

        val transaction = Transaction(
            //TODO: colocar service
            id = UUID.randomUUID(),
            amount = amount,
            createdAt = Instant.now()
        )

        val entry = Entry(
            //TODO: colocar service
            id = UUID.randomUUID(),
            transactionId = transaction.id,
            accountId = accountId,
            type = EntryType.CREDIT,
            amount = amount,
            createdAt = Instant.now()
        )

        return DepositResult(
            transaction = transaction,
            entries = listOf(entry)
        )
    }

    private fun validateAmount(
        amount: BigDecimal
    ) {
        if (amount <= BigDecimal.ZERO) {
            throw InvalidAccountDepositAmountException()
        }
    }
}