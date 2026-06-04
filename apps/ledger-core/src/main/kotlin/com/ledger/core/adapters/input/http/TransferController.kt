package com.ledger.core.adapters.input.http

import com.ledger.core.application.dto.CreateTransferRequest
import com.ledger.core.application.port.input.CreateTransferInputPort
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/transfers")
class TransferController(
    private val createTransferInputPort: CreateTransferInputPort
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun transfer(
        @RequestHeader("Idempotency-Key") idempotencyKey: String,
        @Valid @RequestBody request: CreateTransferRequest
    ) {
        createTransferInputPort.transfer(request, idempotencyKey)
    }
}