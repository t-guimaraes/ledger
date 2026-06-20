package com.tguimaraes.ledger.core.domain.model

import com.tguimaraes.ledger.core.domain.exception.InvalidAccountOwnerNameException
import com.tguimaraes.ledger.core.support.TestFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class AccountTest {

    @Test
    fun `should create account with normalized owner name`() {

        val account = Account.create(
            id = TestFixtures.FROM_ACCOUNT_ID,
            ownerName = " Thiago ",
            createdAt = TestFixtures.CREATED_AT
        )

        assertEquals(TestFixtures.FROM_ACCOUNT_ID, account.id)
        assertEquals("Thiago", account.ownerName)
        assertEquals(TestFixtures.CREATED_AT, account.createdAt)
        assertNull(account.updatedAt)
        assertEquals(0, account.version)
    }

    @Test
    fun `should normalize owner name`() {

        val ownerName = Account.normalizeOwnerName(" Thiago ")

        assertEquals("Thiago", ownerName)
    }

    @Test
    fun `should throw when normalizing blank owner name`() {

        assertThrows(InvalidAccountOwnerNameException::class.java) {
            Account.normalizeOwnerName("   ")
        }
    }

    @Test
    fun `should throw when creating account with blank owner name`() {

        assertThrows(InvalidAccountOwnerNameException::class.java) {
            Account.create(
                id = TestFixtures.FROM_ACCOUNT_ID,
                ownerName = "   ",
                createdAt = TestFixtures.CREATED_AT
            )
        }
    }

    @Test
    fun `should throw when account is instantiated with blank owner name`() {

        assertThrows(InvalidAccountOwnerNameException::class.java) {
            Account(
                id = TestFixtures.FROM_ACCOUNT_ID,
                ownerName = "",
                createdAt = TestFixtures.CREATED_AT,
                updatedAt = null
            )
        }
    }
}
