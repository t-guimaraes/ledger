package com.tguimaraes.ledger.core.application.port.output.query

import com.tguimaraes.ledger.core.application.dto.AccountStatementEntryResult
import java.math.BigDecimal
import java.util.UUID

interface EntryQueryPort {
    fun getBalance(accountId: UUID): BigDecimal
    fun getStatement(accountId: UUID): List<AccountStatementEntryResult>
}