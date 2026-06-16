package com.tguimaraes.ledger.core.integration.entry

import com.tguimaraes.ledger.core.TestcontainersConfiguration
import com.tguimaraes.ledger.core.adapter.outbound.persistence.repository.AccountJpaRepository
import com.tguimaraes.ledger.core.adapter.outbound.persistence.repository.EntryJpaRepository
import com.tguimaraes.ledger.core.adapter.outbound.persistence.repository.TransactionJpaRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(TestcontainersConfiguration::class)
abstract class AbstractIntegrationTest {

    @Autowired
    lateinit var repository: EntryJpaRepository

    @Autowired
    protected lateinit var accountRepository: AccountJpaRepository

    @Autowired
    protected lateinit var transactionRepository: TransactionJpaRepository

    protected fun cleanDatabase() {
        repository.deleteAll()
        transactionRepository.deleteAll()
        accountRepository.deleteAll()
    }
}