package com.tguimaraes.ledger.core.application.port.output

import java.math.BigDecimal
import java.util.UUID

interface EntryQueryPort {
    fun getBalance(accountId: UUID): BigDecimal
}