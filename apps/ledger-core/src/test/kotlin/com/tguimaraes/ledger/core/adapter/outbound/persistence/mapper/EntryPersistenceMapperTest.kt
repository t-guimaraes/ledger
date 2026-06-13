package com.tguimaraes.ledger.core.adapter.outbound.persistence.mapper

import com.tguimaraes.ledger.core.support.TestFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EntryPersistenceMapperTest {

    @Test
    fun `should map entity to domain`() {

        val entity = TestFixtures.debitEntryEntity()

        val domain = EntryPersistenceMapper.toDomain(entity)

        assertEquals(entity.id, domain.id)
        assertEquals(entity.transactionId, domain.transactionId)
        assertEquals(entity.accountId, domain.accountId)
        assertEquals(entity.type, domain.type)
        assertEquals(entity.amount, domain.amount)
        assertEquals(entity.createdAt, domain.createdAt)
    }

    @Test
    fun `should map domain to entity`() {

        val domain = TestFixtures.debitEntry()

        val entity = EntryPersistenceMapper.toEntity(domain)

        assertEquals(domain.id, entity.id)
        assertEquals(domain.transactionId, entity.transactionId)
        assertEquals(domain.accountId, entity.accountId)
        assertEquals(domain.type, entity.type)
        assertEquals(domain.amount, entity.amount)
        assertEquals(domain.createdAt, entity.createdAt)
    }
}