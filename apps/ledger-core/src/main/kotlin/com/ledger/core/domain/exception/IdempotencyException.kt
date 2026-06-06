package com.ledger.core.domain.exception

class IdempotencyException(
    message: String
) : RuntimeException(message)