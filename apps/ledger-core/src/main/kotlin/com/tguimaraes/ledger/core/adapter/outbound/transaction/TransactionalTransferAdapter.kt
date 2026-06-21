package com.tguimaraes.ledger.core.adapter.outbound.transaction

import com.tguimaraes.ledger.core.application.dto.transfer.TransferCommand
import com.tguimaraes.ledger.core.application.port.input.TransferInputPort
import org.springframework.transaction.support.TransactionTemplate

class TransactionalTransferAdapter(
    private val delegate: TransferInputPort,
    private val transactionTemplate: TransactionTemplate
) : TransferInputPort {

    override fun transfer(command: TransferCommand, idempotencyKey: String) {
        transactionTemplate.executeWithoutResult {
            delegate.transfer(command, idempotencyKey)
        }
    }
}
