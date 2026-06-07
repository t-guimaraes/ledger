package com.tguimaraes.ledger.core.adapter.inbound.web.controller

import com.tguimaraes.ledger.core.adapter.inbound.web.doc.TransferApi
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.CreateTransferRequest
import com.tguimaraes.ledger.core.application.mapper.TransferWebMapper
import com.tguimaraes.ledger.core.application.port.input.CreateTransferInputPort
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/transfers")
class TransferController(
    private val createTransferInputPort: CreateTransferInputPort
): TransferApi {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    override fun transfer(
        @RequestHeader("Idempotency-Key") idempotencyKey: String,
        @Valid @RequestBody request: CreateTransferRequest
    ) {
        createTransferInputPort.transfer(
            TransferWebMapper.toCommand(request),
            idempotencyKey)
    }
}