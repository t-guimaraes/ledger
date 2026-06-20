package com.tguimaraes.ledger.core.adapter.inbound.web.dto

import com.tguimaraes.ledger.core.domain.model.EntryType
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class StatementEntryResponse(

    val transactionId: UUID,

    val type: EntryType,

    @field:Schema(example = "100.00")
    val amount: BigDecimal,

    val createdAt: Instant
)