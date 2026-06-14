package com.tguimaraes.ledger.core.adapter.inbound.web.mapper

import com.tguimaraes.ledger.core.support.TestFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TransferMapperTest {

    @Test
    fun `should map request to command`() {

        val request = TestFixtures.createTransferRequest()

        val command = TransferMapper.toCommand(request)

        assertEquals(request.fromAccountId, command.fromAccountId)
        assertEquals(request.toAccountId, command.toAccountId)
        assertEquals(request.amount, command.amount)
    }
}