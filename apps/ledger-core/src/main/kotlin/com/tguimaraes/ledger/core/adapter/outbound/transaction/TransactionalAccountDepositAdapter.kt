package com.tguimaraes.ledger.core.adapter.outbound.transaction

import com.tguimaraes.ledger.core.application.dto.account.AccountDepositCommand
import com.tguimaraes.ledger.core.application.dto.account.AccountDepositResult
import com.tguimaraes.ledger.core.application.port.input.AccountDepositInputPort
import org.springframework.transaction.support.TransactionTemplate
import java.util.*

class TransactionalAccountDepositAdapter(
    private val delegate: AccountDepositInputPort,
    private val transactionTemplate: TransactionTemplate
) : AccountDepositInputPort {

    override fun deposit(command: AccountDepositCommand, accountId: UUID, idempotencyKey: String): AccountDepositResult =
        transactionTemplate.execute {
            delegate.deposit(command, accountId, idempotencyKey)
        } ?: error("Account deposit did not return a result")
}
