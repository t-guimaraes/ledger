package com.tguimaraes.ledger.core.domain.exception

import java.util.UUID

class AccountNotFoundException(
    accountId: UUID
) : RuntimeException(
    "Account $accountId not found"
)