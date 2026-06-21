package com.tguimaraes.ledger.core.adapter.inbound.web.controller

import com.tguimaraes.ledger.core.adapter.inbound.web.doc.TransferApiDoc
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.transfer.TransferRequest
import com.tguimaraes.ledger.core.adapter.inbound.web.mapper.transfer.TransferMapper
import com.tguimaraes.ledger.core.application.port.input.TransferInputPort
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/transfers")
class TransferController(
    private val transferInputPort: TransferInputPort
): TransferApiDoc {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    override fun transfer(
        @RequestHeader("Idempotency-Key") idempotencyKey: String,
        @Valid @RequestBody request: TransferRequest
    ) {
        transferInputPort.transfer(TransferMapper.toCommand(request), idempotencyKey)
    }
}