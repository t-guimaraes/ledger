package com.tguimaraes.ledger.core.application.dto.account

import java.util.*

data class AccountStatementResult(
    val accountId: UUID,
    val entries: List<AccountStatementEntryResult>
)