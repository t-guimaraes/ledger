package com.tguimaraes.ledger.core.integration.support

import com.fasterxml.jackson.databind.ObjectMapper
import com.tguimaraes.ledger.core.adapter.outbound.persistence.entity.AccountJpaEntity
import com.tguimaraes.ledger.core.adapter.outbound.persistence.entity.EntryJpaEntity
import com.tguimaraes.ledger.core.adapter.outbound.persistence.entity.IdempotencyKeyJpaEntity
import com.tguimaraes.ledger.core.adapter.outbound.persistence.entity.TransactionJpaEntity
import com.tguimaraes.ledger.core.adapter.outbound.persistence.repository.*
import com.tguimaraes.ledger.core.domain.model.EntryType
import com.tguimaraes.ledger.core.support.TestcontainersConfiguration
import org.awaitility.Awaitility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestcontainersConfiguration::class)
abstract class AbstractIntegrationTest {

    protected val receivedEvents = CopyOnWriteArrayList<String>()
    protected val receivedEventTypes = CopyOnWriteArrayList<String>()

    protected lateinit var fromAccountId: UUID
    protected lateinit var toAccountId: UUID

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @Autowired
    protected lateinit var entryRepository: EntryJpaRepository

    @Autowired
    protected lateinit var accountRepository: AccountJpaRepository

    @Autowired
    protected lateinit var transactionRepository: TransactionJpaRepository

    @Autowired
    protected lateinit var idempotencyRepository: IdempotencyKeyJpaRepository

    @Autowired
    protected lateinit var outboxEventRepository: OutboxJpaRepository

    protected fun cleanDatabase() {
        idempotencyRepository.deleteAll()
        entryRepository.deleteAll()
        transactionRepository.deleteAll()
        accountRepository.deleteAll()
        outboxEventRepository.deleteAll()
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

    protected fun awaitEvents() {
        Awaitility.await().atMost(Duration.ofSeconds(5)).until {
            receivedEvents.isNotEmpty()
        }
    }
}
