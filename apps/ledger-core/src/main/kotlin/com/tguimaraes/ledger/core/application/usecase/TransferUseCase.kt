package com.tguimaraes.ledger.core.application.usecase

import com.tguimaraes.ledger.core.application.dto.transfer.TransferCommand
import com.tguimaraes.ledger.core.application.port.input.TransferInputPort
import com.tguimaraes.ledger.core.application.port.output.idempotency.IdempotencyPort
import com.tguimaraes.ledger.core.application.port.output.query.EntryQueryPort
import com.tguimaraes.ledger.core.application.port.output.repository.AccountRepositoryPort
import com.tguimaraes.ledger.core.application.port.output.repository.EntryRepositoryPort
import com.tguimaraes.ledger.core.application.port.output.repository.TransactionRepositoryPort
import com.tguimaraes.ledger.core.domain.dto.TransferResult
import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import com.tguimaraes.ledger.core.domain.exception.IdempotencyException
import com.tguimaraes.ledger.core.domain.service.TransferDomainService
import java.util.*

class TransferUseCase(
    private val accountRepositoryPort: AccountRepositoryPort,
    private val transactionRepositoryPort: TransactionRepositoryPort,
    private val entryRepositoryPort: EntryRepositoryPort,
    private val entryQueryPort: EntryQueryPort,
    private val idempotencyPort: IdempotencyPort,
    private val transferDomainService: TransferDomainService
) : TransferInputPort {

    override fun transfer(command: TransferCommand, idempotencyKey: String) {

        validateIdempotency(idempotencyKey)

        val fromAccount = findAccountById(command.fromAccountId)
        val toAccount = findAccountById(command.toAccountId)
        val balance = entryQueryPort.getBalance(fromAccount.id)
        val transferResult = transferDomainService.createTransfer(
            fromAccount,
            toAccount,
            command.amount,
            balance
        )

        persistTransfer(transferResult, idempotencyKey)
    }

    private fun validateIdempotency(idempotencyKey: String) {
        if (idempotencyPort.exists(idempotencyKey)) {
            throw IdempotencyException()
        }
    }

    private fun findAccountById(accountId: UUID) =
        accountRepositoryPort.findById(accountId) ?: throw AccountNotFoundException(accountId)

    private fun persistTransfer(
        transferResult: TransferResult, idempotencyKey: String
    ) {
        transactionRepositoryPort.save(transferResult.transaction)
        entryRepositoryPort.saveAll(transferResult.entries)
        idempotencyPort.save(idempotencyKey)
    }

}
