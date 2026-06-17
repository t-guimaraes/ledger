package com.tguimaraes.ledger.core.integration

import com.tguimaraes.ledger.core.TestcontainersConfiguration
import com.tguimaraes.ledger.core.adapter.outbound.persistence.entity.AccountJpaEntity
import com.tguimaraes.ledger.core.adapter.outbound.persistence.entity.EntryJpaEntity
import com.tguimaraes.ledger.core.adapter.outbound.persistence.entity.TransactionJpaEntity
import com.tguimaraes.ledger.core.adapter.outbound.persistence.repository.AccountJpaRepository
import com.tguimaraes.ledger.core.adapter.outbound.persistence.repository.EntryJpaRepository
import com.tguimaraes.ledger.core.adapter.outbound.persistence.repository.TransactionJpaRepository
import com.tguimaraes.ledger.core.domain.model.EntryType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.StringRedisTemplate
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@SpringBootTest
@Import(TestcontainersConfiguration::class)
abstract class AbstractIntegrationTest {

    @Autowired
    protected lateinit var entryRepository: EntryJpaRepository

    @Autowired
    protected lateinit var accountRepository: AccountJpaRepository

    @Autowired
    protected lateinit var transactionRepository: TransactionJpaRepository

    @Autowired
    protected lateinit var redisTemplate: StringRedisTemplate

    protected lateinit var fromAccountId: UUID
    protected lateinit var toAccountId: UUID

    protected fun cleanDatabase() {
        entryRepository.deleteAll()
        transactionRepository.deleteAll()
        accountRepository.deleteAll()
    }

    protected fun cleanRedis() {
        redisTemplate.connectionFactory
            ?.connection
            ?.serverCommands()
            ?.flushAll()
    }

    protected fun cleanEnvironment() {
        cleanDatabase()
        cleanRedis()
    }

    protected fun createAccount(id: UUID, ownerName: String) {
        accountRepository.save(
            AccountJpaEntity(
                id = id,
                ownerName = ownerName,
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
                version = 0
            )
        )
    }

    protected fun fundAccount(
        accountId: UUID,
        amount: BigDecimal
    ) {
        val transactionId = UUID.randomUUID()

        transactionRepository.save(
            TransactionJpaEntity(
                id = transactionId,
                amount = amount,
                createdAt = Instant.now()
            )
        )

        entryRepository.save(
            EntryJpaEntity(
                id = UUID.randomUUID(),
                transactionId = transactionId,
                accountId = accountId,
                type = EntryType.CREDIT,
                amount = amount,
                createdAt = Instant.now()
            )
        )
    }
}