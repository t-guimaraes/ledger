package com.tguimaraes.ledger.core.adapter.inbound.web.mapper.transfer

import com.tguimaraes.ledger.core.support.TestFixtures
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TransferMapperTest {

    @Test
    fun `should map request to command`() {

        val request = TestFixtures.createTransferRequest()

        val command = TransferMapper.toCommand(request)

        Assertions.assertEquals(request.fromAccountId, command.fromAccountId)
        Assertions.assertEquals(request.toAccountId, command.toAccountId)
        Assertions.assertEquals(request.amount, command.amount)
    }
}