package com.tguimaraes.ledger.core.application.port.output.query

import java.math.BigDecimal
import java.util.UUID

interface EntryQueryPort {
    fun getBalance(accountId: UUID): BigDecimal
}