package com.tguimaraes.ledger.core.adapter.inbound.web.dto

data class ApiErrorResponse(
    val status: Int,
    val code: String,
    val message: String,
    val errors: List<FieldErrorResponse> = emptyList()
)