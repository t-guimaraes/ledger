package com.tguimaraes.ledger.core.application.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import com.tguimaraes.ledger.core.application.dto.account.AccountWithdrawCommand
import com.tguimaraes.ledger.core.application.dto.account.AccountWithdrawResult
import com.tguimaraes.ledger.core.application.port.input.AccountWithdrawInputPort
import com.tguimaraes.ledger.core.application.port.output.idempotency.IdempotencyPort
import com.tguimaraes.ledger.core.application.port.output.query.EntryQueryPort
import com.tguimaraes.ledger.core.application.port.output.repository.AccountRepositoryPort
import com.tguimaraes.ledger.core.application.port.output.repository.EntryRepositoryPort
import com.tguimaraes.ledger.core.application.port.output.repository.OutboxRepositoryPort
import com.tguimaraes.ledger.core.application.port.output.repository.TransactionRepositoryPort
import com.tguimaraes.ledger.core.config.KafkaConfig
import com.tguimaraes.ledger.core.domain.dto.WithdrawResult
import com.tguimaraes.ledger.core.domain.event.EventEnvelope
import com.tguimaraes.ledger.core.domain.event.account.AccountWithdrawEvent
import com.tguimaraes.ledger.core.domain.event.outbox.OutboxEvent
import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import com.tguimaraes.ledger.core.domain.exception.IdempotencyException
import com.tguimaraes.ledger.core.domain.service.AccountDomainService
import java.util.*

class AccountWithdrawUseCase(
    private val accountRepositoryPort: AccountRepositoryPort,
    private val transactionRepositoryPort: TransactionRepositoryPort,
    private val entryRepositoryPort: EntryRepositoryPort,
    private val entryQueryPort: EntryQueryPort,
    private val idempotencyPort: IdempotencyPort,
    private val outboxRepositoryPort: OutboxRepositoryPort,
    private val accountDomainService: AccountDomainService,
    private val objectMapper: ObjectMapper,
    private val kafkaConfig: KafkaConfig
) : AccountWithdrawInputPort {

    override fun withdraw(
        command: AccountWithdrawCommand,
        accountId: UUID,
        idempotencyKey: String
    ): AccountWithdrawResult {

        validateIdempotency(idempotencyKey)

        accountRepositoryPort.findById(accountId) ?: throw AccountNotFoundException(accountId)

        val balance = entryQueryPort.getBalance(accountId)
        val withdrawResult = accountDomainService.withdraw(accountId, command.amount, balance)

        persistWithdraw(withdrawResult, idempotencyKey)
        persistEvent(withdrawResult, accountId)

        return AccountWithdrawResult(
            accountId = accountId,
            amount = withdrawResult.transaction.amount
        )
    }

    private fun validateIdempotency(idempotencyKey: String) {
        if (idempotencyPort.exists(idempotencyKey)) {
            throw IdempotencyException()
        }
    }

    private fun persistWithdraw(withdrawResult: WithdrawResult, idempotencyKey: String) {
        transactionRepositoryPort.save(withdrawResult.transaction)
        entryRepositoryPort.saveAll(withdrawResult.entries)
        idempotencyPort.save(idempotencyKey)
    }

    private fun persistEvent(withdrawResult: WithdrawResult, accountId: UUID) {
        val eventType = AccountWithdrawEvent::class.simpleName!!

        val event = AccountWithdrawEvent(
            transactionId = withdrawResult.transaction.id,
            accountId = accountId,
            amount = withdrawResult.transaction.amount,
            occurredAt = withdrawResult.transaction.createdAt
        )

        val eventEnvelope = EventEnvelope(
            type = eventType,
            version = 1,
            data = event
        )

        outboxRepositoryPort.save(
            OutboxEvent(
                aggregateId = withdrawResult.transaction.id.toString(),
                aggregateType = "WITHDRAW",
                eventType = eventType,
                topic = kafkaConfig.ledgerEventsTopic,
                payload = objectMapper.valueToTree(eventEnvelope)
            )
        )
    }
}