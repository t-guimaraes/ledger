package com.tguimaraes.ledger.core.adapter.inbound.web.doc

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.CreateTransferRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "Transfers")
interface TransferApi {

    @Operation(
        summary = "Create a transfer",
        description = "Transfers money between two accounts"
    )
    fun transfer(

        @Parameter(
            name = "Idempotency-Key",
            description = "Unique key used to guarantee idempotent requests",
            required = true
        )
        idempotencyKey: String,

        request: CreateTransferRequest

    )
}