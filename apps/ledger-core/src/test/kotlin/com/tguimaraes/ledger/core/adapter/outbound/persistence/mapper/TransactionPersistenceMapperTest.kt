package com.tguimaraes.ledger.core.adapter.outbound.persistence.mapper

import com.tguimaraes.ledger.core.support.TestFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TransactionPersistenceMapperTest {

    @Test
    fun `should map entity to domain`() {

        val entity = TestFixtures.transactionEntity()

        val domain =
            TransactionPersistenceMapper.toDomain(entity)

        assertEquals(entity.id, domain.id)
        assertEquals(entity.amount, domain.amount)
        assertEquals(entity.createdAt, domain.createdAt)
    }

    @Test
    fun `should map domain to entity`() {

        val domain = TestFixtures.transaction()

        val entity =
            TransactionPersistenceMapper.toEntity(domain)

        assertEquals(domain.id, entity.id)
        assertEquals(domain.amount, entity.amount)
        assertEquals(domain.createdAt, entity.createdAt)
    }
}