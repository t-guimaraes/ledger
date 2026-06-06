package com.tguimaraes.ledger.core.domain.exception

class IdempotencyException(
    message: String
) : RuntimeException(message)