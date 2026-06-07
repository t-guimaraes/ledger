package com.tguimaraes.ledger.core.application.port.input

import java.math.BigDecimal
import java.util.UUID

interface GetAccountBalanceInputPort {
    fun execute(accountId: UUID): BigDecimal
}