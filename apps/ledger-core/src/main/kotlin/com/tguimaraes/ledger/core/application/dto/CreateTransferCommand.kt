package com.tguimaraes.ledger.core.application.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.util.UUID

data class CreateTransferCommand(

    @field:NotNull
    val fromAccountId: UUID,

    @field:NotNull
    val toAccountId: UUID,

    @field:DecimalMin("0.01")
    val amount: BigDecimal
)