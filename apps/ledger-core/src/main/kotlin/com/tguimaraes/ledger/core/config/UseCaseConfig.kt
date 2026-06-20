package com.tguimaraes.ledger.core.config

import com.tguimaraes.ledger.core.adapter.outbound.transaction.TransactionalCreateAccountAdapter
import com.tguimaraes.ledger.core.adapter.outbound.transaction.TransactionalCreateTransferAdapter
import com.tguimaraes.ledger.core.application.port.input.CreateAccountInputPort
import com.tguimaraes.ledger.core.application.port.input.CreateTransferInputPort
import com.tguimaraes.ledger.core.application.port.input.GetAccountBalanceInputPort
import com.tguimaraes.ledger.core.application.port.input.GetAccountStatementInputPort
import com.tguimaraes.ledger.core.application.port.output.id.IdGeneratorPort
import com.tguimaraes.ledger.core.application.port.output.idempotency.IdempotencyPort
import com.tguimaraes.ledger.core.application.port.output.query.EntryQueryPort
import com.tguimaraes.ledger.core.application.port.output.repository.AccountRepositoryPort
import com.tguimaraes.ledger.core.application.port.output.repository.EntryRepositoryPort
import com.tguimaraes.ledger.core.application.port.output.repository.TransactionRepositoryPort
import com.tguimaraes.ledger.core.application.usecase.CreateAccountUseCase
import com.tguimaraes.ledger.core.application.usecase.CreateTransferUseCase
import com.tguimaraes.ledger.core.application.usecase.GetAccountBalanceUseCase
import com.tguimaraes.ledger.core.application.usecase.GetAccountStatementUseCase
import com.tguimaraes.ledger.core.domain.service.TransferDomainService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import java.time.Clock

@Configuration
class UseCaseConfig {

    @Bean
    fun createAccountUseCase(
        accountRepository: AccountRepositoryPort,
        idGenerator: IdGeneratorPort,
        clock: Clock,
        transactionManager: PlatformTransactionManager
    ): CreateAccountInputPort {
        return TransactionalCreateAccountAdapter(
            CreateAccountUseCase(accountRepository, idGenerator, clock),
            TransactionTemplate(transactionManager)
        )
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
    ): CreateTransferInputPort {
        return TransactionalCreateTransferAdapter(
            CreateTransferUseCase(
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

    @Bean
    fun getAccountBalanceUseCase(
        entryQueryPort: EntryQueryPort,
        accountRepositoryPort: AccountRepositoryPort
    ): GetAccountBalanceInputPort {
        return GetAccountBalanceUseCase(entryQueryPort, accountRepositoryPort)
    }

    @Bean
    fun getAccountStatementUseCase(
        entryQueryPort: EntryQueryPort
    ) : GetAccountStatementInputPort {
        return GetAccountStatementUseCase(entryQueryPort)
    }

    @Bean
    fun clock(): Clock =
        Clock.systemUTC()
}
