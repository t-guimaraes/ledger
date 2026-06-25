package com.tguimaraes.ledger.core.application.usecase

import com.tguimaraes.ledger.core.application.dto.account.AccountWithdrawCommand
import com.tguimaraes.ledger.core.application.dto.account.AccountWithdrawResult
import com.tguimaraes.ledger.core.application.port.input.AccountWithdrawInputPort
import com.tguimaraes.ledger.core.application.port.output.event.EventPublisherPort
import com.tguimaraes.ledger.core.application.port.output.idempotency.IdempotencyPort
import com.tguimaraes.ledger.core.application.port.output.repository.AccountRepositoryPort
import com.tguimaraes.ledger.core.application.port.output.repository.EntryRepositoryPort
import com.tguimaraes.ledger.core.application.port.output.repository.TransactionRepositoryPort
import com.tguimaraes.ledger.core.domain.dto.WithdrawResult
import com.tguimaraes.ledger.core.domain.event.account.AccountWithdrawEvent
import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import com.tguimaraes.ledger.core.domain.exception.IdempotencyException
import com.tguimaraes.ledger.core.domain.service.AccountDomainService
import java.util.*

class AccountWithdrawUseCase(
    private val accountRepositoryPort: AccountRepositoryPort,
    private val transactionRepositoryPort: TransactionRepositoryPort,
    private val entryRepositoryPort: EntryRepositoryPort,
    private val idempotencyPort: IdempotencyPort,
    private val eventPublisherPort: EventPublisherPort,
    private val accountDomainService: AccountDomainService
) : AccountWithdrawInputPort {

    override fun withdraw(
        command: AccountWithdrawCommand,
        accountId: UUID,
        idempotencyKey: String
    ): AccountWithdrawResult {

        validateIdempotency(idempotencyKey)
        accountRepositoryPort.findById(accountId) ?: throw AccountNotFoundException(accountId)

        val withdraw = accountDomainService.withdraw(
            accountId,
            command.amount
        )

        persistWithdraw(withdraw, idempotencyKey)

        eventPublisherPort.publish(
            AccountWithdrawEvent(
                transactionId = withdraw.transaction.id,
                accountId = accountId,
                amount = withdraw.transaction.amount,
                occurredAt = withdraw.transaction.createdAt
            )
        )

        return AccountWithdrawResult(
            accountId,
            withdraw.transaction.amount
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
}