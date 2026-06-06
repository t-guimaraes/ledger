package com.tguimaraes.ledger.core.application.usecase

import com.tguimaraes.ledger.core.application.dto.CreateTransferRequest
import com.tguimaraes.ledger.core.application.port.input.CreateTransferInputPort
import com.tguimaraes.ledger.core.application.port.output.AccountRepositoryPort
import com.tguimaraes.ledger.core.application.port.output.EntryRepositoryPort
import com.tguimaraes.ledger.core.application.port.output.IdempotencyPort
import com.tguimaraes.ledger.core.application.port.output.TransactionRepositoryPort
import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import com.tguimaraes.ledger.core.domain.exception.IdempotencyException
import com.tguimaraes.ledger.core.domain.model.Entry
import com.tguimaraes.ledger.core.domain.model.EntryType
import com.tguimaraes.ledger.core.domain.model.Transaction
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class CreateTransferUseCase(
    private val accountRepositoryPort: AccountRepositoryPort,
    private val transactionRepositoryPort: TransactionRepositoryPort,
    private val entryRepositoryPort: EntryRepositoryPort,
    private val idempotencyPort: IdempotencyPort
) : CreateTransferInputPort {

    @Transactional
    override fun transfer(request: CreateTransferRequest, idempotencyKey: String) {
        if (idempotencyPort.exists(idempotencyKey)) {
            throw IdempotencyException(
                "Request already processed"
            )
        }

        val fromAccount = accountRepositoryPort.findById(request.fromAccountId)
            ?: throw AccountNotFoundException()

        val toAccount = accountRepositoryPort.findById(request.toAccountId)
            ?: throw AccountNotFoundException()

        val transaction = Transaction(
            id = UUID.randomUUID(),
            amount = request.amount,
            createdAt = Instant.now()
        )

        transactionRepositoryPort.save(transaction)

        val entries = listOf(

            Entry(
                id = UUID.randomUUID(),
                transactionId = transaction.id,
                accountId = fromAccount.id,
                type = EntryType.DEBIT,
                amount = request.amount,
                createdAt = Instant.now()
            ),

            Entry(
                id = UUID.randomUUID(),
                transactionId = transaction.id,
                accountId = toAccount.id,
                type = EntryType.CREDIT,
                amount = request.amount,
                createdAt = Instant.now()
            )
        )

        entryRepositoryPort.saveAll(entries)
        idempotencyPort.save(idempotencyKey)
    }
}