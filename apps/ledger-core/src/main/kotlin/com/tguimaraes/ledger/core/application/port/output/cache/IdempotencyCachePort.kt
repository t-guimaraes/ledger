package com.tguimaraes.ledger.core.application.port.output.cache

interface IdempotencyCachePort {

    fun exists(key: String): Boolean

    fun save(key: String)
}