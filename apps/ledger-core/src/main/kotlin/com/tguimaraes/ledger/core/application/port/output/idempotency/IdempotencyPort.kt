package com.tguimaraes.ledger.core.application.port.output.idempotency

interface IdempotencyPort {

    fun exists(key: String): Boolean

    fun save(key: String)
}
