package com.tguimaraes.ledger.core.adapter.outbound.persistence.mapper

import com.tguimaraes.ledger.core.support.TestFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AccountPersistenceMapperTest {

    @Test
    fun `should map entity to domain`() {

        val entity = TestFixtures.fromAccountEntity()

        val domain = AccountPersistenceMapper.toDomain(entity)

        assertEquals(entity.id, domain.id)
        assertEquals(entity.ownerName, domain.ownerName)
        assertEquals(entity.createdAt, domain.createdAt)
        assertEquals(entity.updatedAt, domain.updatedAt)
        assertEquals(entity.version, domain.version)
    }

    @Test
    fun `should map domain to entity`() {

        val domain = TestFixtures.fromAccount()

        val entity = AccountPersistenceMapper.toEntity(domain)

        assertEquals(domain.id, entity.id)
        assertEquals(domain.ownerName, entity.ownerName)
        assertEquals(domain.createdAt, entity.createdAt)
        assertEquals(domain.updatedAt, entity.updatedAt)
        assertEquals(domain.version, entity.version)
    }
}