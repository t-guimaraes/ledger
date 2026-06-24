package com.tguimaraes.ledger.core.domain.event

data class EventEnvelope<T>(
    val type: String,
    val version: Int = 1,
    val data: T
)