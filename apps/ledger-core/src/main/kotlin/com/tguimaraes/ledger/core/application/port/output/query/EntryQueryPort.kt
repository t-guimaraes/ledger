package com.tguimaraes.ledger.core.application.port.output.query

import com.tguimaraes.ledger.core.application.dto.account.AccountStatementEntryResult
import java.math.BigDecimal
import java.util.*

interface EntryQueryPort {
    fun getBalance(accountId: UUID): BigDecimal
    fun getStatement(accountId: UUID): List<AccountStatementEntryResult>
}