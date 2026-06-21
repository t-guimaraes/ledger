package com.tguimaraes.ledger.core.adapter.outbound.transaction

import com.tguimaraes.ledger.core.application.dto.account.AccountWithdrawCommand
import com.tguimaraes.ledger.core.application.dto.account.AccountWithdrawResult
import com.tguimaraes.ledger.core.application.port.input.AccountWithdrawInputPort
import org.springframework.transaction.support.TransactionTemplate
import java.util.*

class TransactionalAccountWithdrawAdapter(
    private val delegate: AccountWithdrawInputPort,
    private val transactionTemplate: TransactionTemplate
) : AccountWithdrawInputPort {

    override fun withdraw(command: AccountWithdrawCommand, accountId: UUID, idempotencyKey: String): AccountWithdrawResult =
        transactionTemplate.execute {
            delegate.withdraw(command, accountId, idempotencyKey)
        } ?: error("Account withdraw did not return a result")
}
