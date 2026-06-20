package com.tguimaraes.ledger.core.adapter.inbound.web.dto

data class FieldErrorResponse(
    val field: String,
    val message: String
)