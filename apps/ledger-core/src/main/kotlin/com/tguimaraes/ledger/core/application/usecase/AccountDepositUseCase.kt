package com.tguimaraes.ledger.core.application.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import com.tguimaraes.ledger.core.application.dto.account.AccountDepositCommand
import com.tguimaraes.ledger.core.application.dto.account.AccountDepositResult
import com.tguimaraes.ledger.core.application.port.input.AccountDepositInputPort
import com.tguimaraes.ledger.core.application.port.output.idempotency.IdempotencyPort
import com.tguimaraes.ledger.core.application.port.output.repository.AccountRepositoryPort
import com.tguimaraes.ledger.core.application.port.output.repository.EntryRepositoryPort
import com.tguimaraes.ledger.core.application.port.output.repository.OutboxRepositoryPort
import com.tguimaraes.ledger.core.application.port.output.repository.TransactionRepositoryPort
import com.tguimaraes.ledger.core.config.KafkaConfig
import com.tguimaraes.ledger.core.domain.dto.DepositResult
import com.tguimaraes.ledger.core.domain.event.EventEnvelope
import com.tguimaraes.ledger.core.domain.event.account.AccountDepositEvent
import com.tguimaraes.ledger.core.domain.event.outbox.OutboxEvent
import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import com.tguimaraes.ledger.core.domain.exception.IdempotencyException
import com.tguimaraes.ledger.core.domain.service.AccountDomainService
import java.util.*

class AccountDepositUseCase(
    private val accountRepositoryPort: AccountRepositoryPort,
    private val transactionRepositoryPort: TransactionRepositoryPort,
    private val entryRepositoryPort: EntryRepositoryPort,
    private val idempotencyPort: IdempotencyPort,
    private val outboxRepositoryPort: OutboxRepositoryPort,
    private val accountDomainService: AccountDomainService,
    private val objectMapper: ObjectMapper,
    private val kafkaConfig: KafkaConfig
) : AccountDepositInputPort {

    override fun deposit(
        command: AccountDepositCommand,
        accountId: UUID,
        idempotencyKey: String
    ): AccountDepositResult {

        validateIdempotency(idempotencyKey)
        accountRepositoryPort.findById(accountId) ?: throw AccountNotFoundException(accountId)
        val depositResult = accountDomainService.deposit(accountId, command.amount)

        persistDeposit(depositResult, idempotencyKey)
        persistEvent(depositResult, accountId)

        return AccountDepositResult(
            accountId = accountId,
            amount = depositResult.transaction.amount
        )
    }

    private fun validateIdempotency(idempotencyKey: String) {
        if (idempotencyPort.exists(idempotencyKey)) {
            throw IdempotencyException()
        }
    }

    private fun persistDeposit(depositResult: DepositResult, idempotencyKey: String) {
        transactionRepositoryPort.save(depositResult.transaction)
        entryRepositoryPort.saveAll(depositResult.entries)
        idempotencyPort.save(idempotencyKey)
    }

    private fun persistEvent(depositResult: DepositResult, accountId: UUID) {
        val eventType = AccountDepositEvent::class.simpleName!!

        val event = AccountDepositEvent(
            transactionId = depositResult.transaction.id,
            accountId = accountId,
            amount = depositResult.transaction.amount,
            occurredAt = depositResult.transaction.createdAt
        )

        val eventEnvelope = EventEnvelope(
            type = eventType,
            version = 1,
            data = event
        )

        outboxRepositoryPort.save(
            OutboxEvent(
                aggregateId = depositResult.transaction.id.toString(),
                aggregateType = "DEPOSIT",
                eventType = eventType,
                topic = kafkaConfig.ledgerEventsTopic,
                payload = objectMapper.valueToTree(eventEnvelope)
            )
        )
    }
}