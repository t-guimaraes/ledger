package com.tguimaraes.ledger.core.config

import com.tguimaraes.ledger.core.adapter.outbound.transaction.TransactionalAccountDepositAdapter
import com.tguimaraes.ledger.core.adapter.outbound.transaction.TransactionalAccountWithdrawAdapter
import com.tguimaraes.ledger.core.adapter.outbound.transaction.TransactionalCreateAccountAdapter
import com.tguimaraes.ledger.core.adapter.outbound.transaction.TransactionalTransferAdapter
import com.tguimaraes.ledger.core.application.port.input.*
import com.tguimaraes.ledger.core.application.port.output.idempotency.IdempotencyPort
import com.tguimaraes.ledger.core.application.port.output.query.EntryQueryPort
import com.tguimaraes.ledger.core.application.port.output.repository.AccountRepositoryPort
import com.tguimaraes.ledger.core.application.port.output.repository.EntryRepositoryPort
import com.tguimaraes.ledger.core.application.port.output.repository.TransactionRepositoryPort
import com.tguimaraes.ledger.core.application.usecase.*
import com.tguimaraes.ledger.core.domain.service.AccountDomainService
import com.tguimaraes.ledger.core.domain.service.TransferDomainService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate

@Configuration
class UseCaseConfig {

    @Bean
    fun createAccountUseCase(
        accountRepository: AccountRepositoryPort,
        transactionManager: PlatformTransactionManager
    ): CreateAccountInputPort {
        return TransactionalCreateAccountAdapter(
            CreateAccountUseCase(accountRepository),
            TransactionTemplate(transactionManager)
        )
    }

    @Bean
    fun accountDeposit(
        accountRepositoryPort: AccountRepositoryPort,
        transactionRepositoryPort: TransactionRepositoryPort,
        entryRepositoryPort: EntryRepositoryPort,
        idempotencyPort: IdempotencyPort,
        accountDomainService: AccountDomainService,
        transactionManager: PlatformTransactionManager
    ): AccountDepositInputPort {
        return TransactionalAccountDepositAdapter(
            AccountDepositUseCase(
                accountRepositoryPort,
                transactionRepositoryPort,
                entryRepositoryPort,
                idempotencyPort,
                accountDomainService),
            TransactionTemplate(transactionManager)
        )
    }

    @Bean
    fun accountWithdraw(
        accountRepositoryPort: AccountRepositoryPort,
        transactionRepositoryPort: TransactionRepositoryPort,
        entryRepositoryPort: EntryRepositoryPort,
        idempotencyPort: IdempotencyPort,
        accountDomainService: AccountDomainService,
        transactionManager: PlatformTransactionManager
    ): AccountWithdrawInputPort {
        return TransactionalAccountWithdrawAdapter(
            AccountWithdrawUseCase(
                accountRepositoryPort,
                transactionRepositoryPort,
                entryRepositoryPort,
                idempotencyPort,
                accountDomainService
            ),
            TransactionTemplate(transactionManager)
        )
    }

    @Bean
    fun getAccountBalanceUseCase(
        entryQueryPort: EntryQueryPort,
        accountRepositoryPort: AccountRepositoryPort
    ): AccountBalanceInputPort {
        return AccountBalanceUseCase(entryQueryPort, accountRepositoryPort)
    }

    @Bean
    fun getAccountStatementUseCase(
        entryQueryPort: EntryQueryPort,
        accountRepositoryPort: AccountRepositoryPort
    ) : AccountStatementInputPort {
        return AccountStatementUseCase(entryQueryPort, accountRepositoryPort)
    }

    @Bean
    fun createTransferUseCase(
        accountRepositoryPort: AccountRepositoryPort,
        transactionRepositoryPort: TransactionRepositoryPort,
        entryRepositoryPort: EntryRepositoryPort,
        entryQueryPort: EntryQueryPort,
        idempotencyPort: IdempotencyPort,
        transferDomainService: TransferDomainService,
        transactionManager: PlatformTransactionManager
    ): TransferInputPort {
        return TransactionalTransferAdapter(
            TransferUseCase(
                accountRepositoryPort,
                transactionRepositoryPort,
                entryRepositoryPort,
                entryQueryPort,
                idempotencyPort,
                transferDomainService
            ),
            TransactionTemplate(transactionManager)
        )
    }
}
