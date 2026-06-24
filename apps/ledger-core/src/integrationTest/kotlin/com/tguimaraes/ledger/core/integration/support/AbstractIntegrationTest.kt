package com.tguimaraes.ledger.core.integration.support

import com.tguimaraes.ledger.core.adapter.outbound.persistence.entity.AccountJpaEntity
import com.tguimaraes.ledger.core.adapter.outbound.persistence.entity.EntryJpaEntity
import com.tguimaraes.ledger.core.adapter.outbound.persistence.entity.IdempotencyKeyJpaEntity
import com.tguimaraes.ledger.core.adapter.outbound.persistence.entity.TransactionJpaEntity
import com.tguimaraes.ledger.core.adapter.outbound.persistence.repository.AccountJpaRepository
import com.tguimaraes.ledger.core.adapter.outbound.persistence.repository.EntryJpaRepository
import com.tguimaraes.ledger.core.adapter.outbound.persistence.repository.IdempotencyKeyJpaRepository
import com.tguimaraes.ledger.core.adapter.outbound.persistence.repository.TransactionJpaRepository
import com.tguimaraes.ledger.core.domain.model.EntryType
import com.tguimaraes.ledger.core.support.TestcontainersConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.kafka.KafkaContainer
import org.testcontainers.utility.DockerImageName
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration::class)
abstract class AbstractIntegrationTest {

    @Autowired
    protected lateinit var entryRepository: EntryJpaRepository

    @Autowired
    protected lateinit var accountRepository: AccountJpaRepository

    @Autowired
    protected lateinit var transactionRepository: TransactionJpaRepository

    @Autowired
    protected lateinit var idempotencyRepository: IdempotencyKeyJpaRepository

    protected lateinit var fromAccountId: UUID
    protected lateinit var toAccountId: UUID

    companion object {

        @JvmStatic
        val kafkaContainer = KafkaContainer(
            DockerImageName.parse("apache/kafka:4.0.0")
        ).apply {
            start()
        }
    }

    protected fun cleanDatabase() {
        idempotencyRepository.deleteAll()
        entryRepository.deleteAll()
        transactionRepository.deleteAll()
        accountRepository.deleteAll()
    }

    protected fun cleanEnvironment() {
        cleanDatabase()
    }

    protected fun createIdempotencyKey(key: String) {
        idempotencyRepository.save(
            IdempotencyKeyJpaEntity(
                key = key,
                createdAt = Instant.now()
            )
        )
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
