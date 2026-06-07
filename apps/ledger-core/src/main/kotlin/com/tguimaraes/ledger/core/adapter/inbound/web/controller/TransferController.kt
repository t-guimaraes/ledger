package com.tguimaraes.ledger.core.adapter.inbound.web.controller

import com.tguimaraes.ledger.core.application.dto.CreateTransferCommand
import com.tguimaraes.ledger.core.application.port.input.CreateTransferInputPort
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
        @Valid @RequestBody request: CreateTransferCommand
    ) {
        createTransferInputPort.transfer(request, idempotencyKey)
    }
}