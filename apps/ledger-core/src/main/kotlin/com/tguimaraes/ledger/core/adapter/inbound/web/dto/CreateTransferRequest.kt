package com.tguimaraes.ledger.core.adapter.inbound.web.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.util.UUID

data class CreateTransferRequest(

    @field:Schema(example = "11111111-1111-1111-1111-111111111111")
    @field:NotNull(message = "From account must not be null")
    val fromAccountId: UUID,

    @field:Schema(example = "22222222-2222-2222-2222-222222222222")
    @field:NotNull(message = "To account must not be null")
    val toAccountId: UUID,

    @field:Schema(example = "100.00")
    @field:DecimalMin("0.01", "Value must be greater than 0")
    val amount: BigDecimal
)
