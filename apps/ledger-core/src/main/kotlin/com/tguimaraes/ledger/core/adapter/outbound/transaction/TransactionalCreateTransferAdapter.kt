package com.tguimaraes.ledger.core.adapter.outbound.transaction

import com.tguimaraes.ledger.core.application.dto.CreateTransferCommand
import com.tguimaraes.ledger.core.application.port.input.CreateTransferInputPort
import org.springframework.transaction.support.TransactionTemplate

class TransactionalCreateTransferAdapter(
    private val delegate: CreateTransferInputPort,
    private val transactionTemplate: TransactionTemplate
) : CreateTransferInputPort {

    override fun transfer(command: CreateTransferCommand, idempotencyKey: String) {
        transactionTemplate.executeWithoutResult {
            delegate.transfer(command, idempotencyKey)
        }
    }
}
