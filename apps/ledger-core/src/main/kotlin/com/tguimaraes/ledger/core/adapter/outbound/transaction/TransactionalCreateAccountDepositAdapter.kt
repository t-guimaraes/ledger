package com.tguimaraes.ledger.core.adapter.outbound.transaction

import com.tguimaraes.ledger.core.application.dto.CreateAccountDepositCommand
import com.tguimaraes.ledger.core.application.dto.CreateAccountDepositResult
import com.tguimaraes.ledger.core.application.port.input.CreateAccountDepositInputPort
import org.springframework.transaction.support.TransactionTemplate
import java.util.UUID

class TransactionalCreateAccountDepositAdapter(
    private val delegate: CreateAccountDepositInputPort,
    private val transactionTemplate: TransactionTemplate
) : CreateAccountDepositInputPort {

    override fun deposit(command: CreateAccountDepositCommand, accountId: UUID, idempotencyKey: String): CreateAccountDepositResult =
        transactionTemplate.execute {
            delegate.deposit(command, accountId, idempotencyKey)
        } ?: error("Account deposit did not return a result")
}
