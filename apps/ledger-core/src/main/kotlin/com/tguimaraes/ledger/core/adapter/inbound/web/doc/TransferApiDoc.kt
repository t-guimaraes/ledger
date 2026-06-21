package com.tguimaraes.ledger.core.adapter.inbound.web.doc

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.transfer.TransferRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "Transfers")
interface TransferApiDoc {

    @Operation(
        summary = "Create a transfer",
        description = "Transfers money between two accounts"
    )
    fun transfer(
        @Parameter(
            description = "Unique key used to guarantee idempotent requests",
            example = "Idempotency-Key",
            required = true
        )
        idempotencyKey: String,
        request: TransferRequest
    )
}