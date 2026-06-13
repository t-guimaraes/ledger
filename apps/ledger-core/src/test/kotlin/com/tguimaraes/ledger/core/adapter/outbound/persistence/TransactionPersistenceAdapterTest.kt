package com.tguimaraes.ledger.core.adapter.outbound.persistence

import com.tguimaraes.ledger.core.adapter.outbound.persistence.repository.TransactionJpaRepository
import com.tguimaraes.ledger.core.support.TestFixtures
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TransactionPersistenceAdapterTest {

    private val repository = mockk<TransactionJpaRepository>()

    private lateinit var adapter: TransactionPersistenceAdapter

    @BeforeEach
    fun setup() {
        adapter = TransactionPersistenceAdapter(repository)
    }

    @Test
    fun `should save transaction`() {

        val transaction = TestFixtures.transaction()

        every {
            repository.save(any())
        } answers { firstArg() }

        adapter.save(transaction)

        verify(exactly = 1) {
            repository.save(
                match {
                    it.id == transaction.id &&
                            it.amount == transaction.amount
                }
            )
        }
    }
}