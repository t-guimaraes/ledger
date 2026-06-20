package com.tguimaraes.ledger.core.adapter.outbound.transaction

import com.tguimaraes.ledger.core.application.dto.CreateAccountCommand
import com.tguimaraes.ledger.core.application.dto.CreateAccountResult
import com.tguimaraes.ledger.core.application.port.input.CreateAccountInputPort
import org.springframework.transaction.support.TransactionTemplate

class TransactionalCreateAccountAdapter(
    private val delegate: CreateAccountInputPort,
    private val transactionTemplate: TransactionTemplate
) : CreateAccountInputPort {

    override fun execute(command: CreateAccountCommand): CreateAccountResult =
        transactionTemplate.execute {
            delegate.execute(command)
        } ?: error("Create account transaction did not return a result")
}
