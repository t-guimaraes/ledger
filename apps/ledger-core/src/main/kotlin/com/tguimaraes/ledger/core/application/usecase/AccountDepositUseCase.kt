package com.tguimaraes.ledger.core.application.usecase

import com.tguimaraes.ledger.core.application.dto.account.AccountDepositCommand
import com.tguimaraes.ledger.core.application.dto.account.AccountDepositResult
import com.tguimaraes.ledger.core.application.port.input.AccountDepositInputPort
import com.tguimaraes.ledger.core.application.port.output.idempotency.IdempotencyPort
import com.tguimaraes.ledger.core.application.port.output.repository.AccountRepositoryPort
import com.tguimaraes.ledger.core.application.port.output.repository.EntryRepositoryPort
import com.tguimaraes.ledger.core.application.port.output.repository.TransactionRepositoryPort
import com.tguimaraes.ledger.core.domain.dto.DepositResult
import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import com.tguimaraes.ledger.core.domain.exception.IdempotencyException
import com.tguimaraes.ledger.core.domain.service.AccountDomainService
import java.util.*

class AccountDepositUseCase(
    private val accountRepositoryPort: AccountRepositoryPort,
    private val transactionRepositoryPort: TransactionRepositoryPort,
    private val entryRepositoryPort: EntryRepositoryPort,
    private val idempotencyPort: IdempotencyPort,
    private val accountDomainService: AccountDomainService
) : AccountDepositInputPort {

    override fun deposit(
        command: AccountDepositCommand,
        accountId: UUID,
        idempotencyKey: String
    ): AccountDepositResult {

        validateIdempotency(idempotencyKey)
        accountRepositoryPort.findById(accountId) ?: throw AccountNotFoundException(accountId)

        val deposit = accountDomainService.createDeposit(
            accountId,
            command.amount
        )

        persistDeposit(deposit, idempotencyKey)

        return AccountDepositResult(
            accountId,
            deposit.transaction.amount
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
}