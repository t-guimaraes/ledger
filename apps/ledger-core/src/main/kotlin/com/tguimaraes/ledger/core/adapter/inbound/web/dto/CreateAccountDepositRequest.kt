package com.tguimaraes.ledger.core.adapter.inbound.web.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class CreateAccountDepositRequest (
    @field:NotNull
    @field:DecimalMin("0.01")
    val amount: BigDecimal
)