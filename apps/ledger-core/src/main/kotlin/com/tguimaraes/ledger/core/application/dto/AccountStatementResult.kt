package com.tguimaraes.ledger.core.application.dto

import java.util.UUID

data class AccountStatementResult(
    val accountId: UUID,
    val entries: List<AccountStatementEntryResult>
)