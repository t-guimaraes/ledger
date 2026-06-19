package com.tguimaraes.ledger.core.application.usecase

import com.tguimaraes.ledger.core.application.dto.CreateTransferCommand
import com.tguimaraes.ledger.core.application.port.input.CreateTransferInputPort
import com.tguimaraes.ledger.core.application.port.output.repository.AccountRepositoryPort
import com.tguimaraes.ledger.core.application.port.output.query.EntryQueryPort
import com.tguimaraes.ledger.core.application.port.output.repository.EntryRepositoryPort
import com.tguimaraes.ledger.core.application.port.output.cache.IdempotencyCachePort
import com.tguimaraes.ledger.core.application.port.output.repository.TransactionRepositoryPort
import com.tguimaraes.ledger.core.domain.dto.TransferResult
import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import com.tguimaraes.ledger.core.domain.exception.IdempotencyException
import com.tguimaraes.ledger.core.domain.service.TransferDomainService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CreateTransferUseCase(
    private val accountRepositoryPort: AccountRepositoryPort,
    private val transactionRepositoryPort: TransactionRepositoryPort,
    private val entryRepositoryPort: EntryRepositoryPort,
    private val entryQueryPort: EntryQueryPort,
    private val idempotencyCachePort: IdempotencyCachePort,
    private val transferDomainService: TransferDomainService
) : CreateTransferInputPort {

    @Transactional
    override fun transfer(command: CreateTransferCommand, idempotencyKey: String) {

        validateIdempotency(idempotencyKey)
        val fromAccount = getAccountById(command.fromAccountId)
        val toAccount = getAccountById(command.toAccountId)

        val balance =
            entryQueryPort.getBalance(
                fromAccount.id
            )

        val transferResult = transferDomainService.createTransfer(
            fromAccount,
            toAccount,
            command.amount,
            balance
        )

        persistTransfer(transferResult, idempotencyKey)
    }

    private fun validateIdempotency(idempotencyKey: String) {
        if (idempotencyCachePort.exists(idempotencyKey)) {
            throw IdempotencyException(
                "Request already processed"
            )
        }
    }

    private fun getAccountById(accountId: UUID) =
        accountRepositoryPort.findById(accountId) ?: throw AccountNotFoundException(accountId)

    private fun persistTransfer(
        transferResult: TransferResult, idempotencyKey: String
    ) {
        transactionRepositoryPort.save(
            transferResult.transaction
        )

        entryRepositoryPort.saveAll(
            transferResult.entries
        )

        idempotencyCachePort.save(
            idempotencyKey
        )
    }

}