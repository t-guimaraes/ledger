package com.tguimaraes.ledger.core.domain.exception

class IdempotencyException :
    RuntimeException("Request already processed")