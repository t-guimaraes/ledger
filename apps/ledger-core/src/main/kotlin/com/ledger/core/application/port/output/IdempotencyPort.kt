package com.ledger.core.application.port.output

interface IdempotencyPort {

    fun exists(key: String): Boolean

    fun save(key: String)
}