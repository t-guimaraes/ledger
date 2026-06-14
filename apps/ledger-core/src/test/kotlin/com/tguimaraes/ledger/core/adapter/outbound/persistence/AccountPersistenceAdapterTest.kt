package com.tguimaraes.ledger.core.adapter.outbound.persistence

import com.tguimaraes.ledger.core.adapter.outbound.persistence.repository.AccountJpaRepository
import com.tguimaraes.ledger.core.support.TestFixtures
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class AccountPersistenceAdapterTest {
    private val repository = mockk<AccountJpaRepository>()

    private lateinit var adapter: AccountPersistenceAdapter

    @BeforeEach
    fun setup() {
        adapter = AccountPersistenceAdapter(repository)
    }

    @Test
    fun `should return account when found`() {

        val account = TestFixtures.fromAccount()

        val entity = TestFixtures.fromAccountEntity()

        every {
            repository.findById(account.id)
        } returns Optional.of(entity)

        val result = adapter.findById(account.id)

        assertNotNull(result)
        assertEquals(account.id, result!!.id)

        verify(exactly = 1) {
            repository.findById(account.id)
        }
    }

    @Test
    fun `should return null when account does not exist`() {

        val accountId = TestFixtures.FROM_ACCOUNT_ID

        every {
            repository.findById(accountId)
        } returns Optional.empty()

        val result = adapter.findById(accountId)

        assertNull(result)

        verify(exactly = 1) {
            repository.findById(accountId)
        }
    }

    @Test
    fun `should save account`() {

        val account = TestFixtures.fromAccount()

        every {
            repository.save(any())
        } answers { firstArg() }

        adapter.save(account)

        verify(exactly = 1) {
            repository.save(
                match {
                    it.id == account.id &&
                            it.ownerName == account.ownerName &&
                                it.version == account.version
                }
            )
        }
    }
}