package com.tguimaraes.ledger.core.application.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import com.tguimaraes.ledger.core.application.dto.transfer.TransferCommand
import com.tguimaraes.ledger.core.application.port.input.TransferInputPort
import com.tguimaraes.ledger.core.application.port.output.idempotency.IdempotencyPort
import com.tguimaraes.ledger.core.application.port.output.query.EntryQueryPort
import com.tguimaraes.ledger.core.application.port.output.repository.AccountRepositoryPort
import com.tguimaraes.ledger.core.application.port.output.repository.EntryRepositoryPort
import com.tguimaraes.ledger.core.application.port.output.repository.OutboxRepositoryPort
import com.tguimaraes.ledger.core.application.port.output.repository.TransactionRepositoryPort
import com.tguimaraes.ledger.core.config.KafkaConfig
import com.tguimaraes.ledger.core.domain.dto.TransferResult
import com.tguimaraes.ledger.core.domain.event.EventEnvelope
import com.tguimaraes.ledger.core.domain.event.outbox.OutboxEvent
import com.tguimaraes.ledger.core.domain.event.transfer.TransferEvent
import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import com.tguimaraes.ledger.core.domain.exception.IdempotencyException
import com.tguimaraes.ledger.core.domain.model.Account
import com.tguimaraes.ledger.core.domain.service.TransferDomainService
import java.util.*

class TransferUseCase(
    private val accountRepositoryPort: AccountRepositoryPort,
    private val transactionRepositoryPort: TransactionRepositoryPort,
    private val entryRepositoryPort: EntryRepositoryPort,
    private val entryQueryPort: EntryQueryPort,
    private val idempotencyPort: IdempotencyPort,
    private val outboxRepositoryPort: OutboxRepositoryPort,
    private val transferDomainService: TransferDomainService,
    private val objectMapper: ObjectMapper,
    private val kafkaConfig: KafkaConfig,
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
        persistEvent(transferResult, fromAccount, toAccount)
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

    private fun persistEvent(transferResult: TransferResult, fromAccount: Account, toAccount: Account) {
        val eventType = TransferEvent::class.simpleName!!

        val event = TransferEvent(
            transactionId = transferResult.transaction.id,
            fromAccountId = fromAccount.id,
            toAccountId = toAccount.id,
            amount = transferResult.transaction.amount,
            occurredAt = transferResult.transaction.createdAt
        )

        val eventEnvelope = EventEnvelope(
            eventType,
            1,
            data = event
        )

        outboxRepositoryPort.save(
            OutboxEvent(
                aggregateId = transferResult.transaction.id.toString(),
                aggregateType = "TRANSFER",
                eventType = eventType,
                topic = kafkaConfig.ledgerEventsTopic,
                payload = objectMapper.valueToTree(eventEnvelope)
            )
        )
    }
}
