package com.tguimaraes.ledger.core.adapter.outbound.persistence

import com.tguimaraes.ledger.core.adapter.outbound.persistence.entity.EntryJpaEntity
import com.tguimaraes.ledger.core.adapter.outbound.persistence.repository.EntryJpaRepository
import com.tguimaraes.ledger.core.support.TestFixtures
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EntryPersistenceAdapterTest {

    private val repository = mockk<EntryJpaRepository>()

    private lateinit var adapter: EntryPersistenceAdapter

    @BeforeEach
    fun setup() {
        adapter = EntryPersistenceAdapter(repository)
    }

    @Test
    fun `should save all entries`() {

        val entries = listOf(
            TestFixtures.debitEntry(),
            TestFixtures.creditEntry()
        )

        every {
            repository.saveAll(any<Iterable<EntryJpaEntity>>())
        } returns emptyList<EntryJpaEntity>()

        adapter.saveAll(entries)

        verify(exactly = 1) {
            repository.saveAll(any<Iterable<EntryJpaEntity>>())
        }
    }
}
