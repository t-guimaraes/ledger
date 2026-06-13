package com.tguimaraes.ledger.core.adapter.outbound.persistence

import com.tguimaraes.ledger.core.adapter.outbound.persistence.repository.EntryJpaRepository
import com.tguimaraes.ledger.core.support.TestFixtures
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class EntryQueryPersistenceAdapterTest {

    private val repository = mockk<EntryJpaRepository>()

    private lateinit var adapter: EntryQueryPersistenceAdapter

    @BeforeEach
    fun setup() {
        adapter = EntryQueryPersistenceAdapter(repository)
    }

    @Test
    fun `should return balance from repository`() {

        val accountId = TestFixtures.FROM_ACCOUNT_ID

        every {
            repository.getBalance(accountId)
        } returns BigDecimal("1500.00")

        val result = adapter.getBalance(accountId)

        assertEquals(BigDecimal("1500.00"), result)

        verify(exactly = 1) {
            repository.getBalance(accountId)
        }
    }
}