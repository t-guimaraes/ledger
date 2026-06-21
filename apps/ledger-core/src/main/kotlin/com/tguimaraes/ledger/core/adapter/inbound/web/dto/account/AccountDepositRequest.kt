package com.tguimaraes.ledger.core.adapter.inbound.web.dto.account

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class AccountDepositRequest (
    @field:NotNull
    @field:DecimalMin("0.01")
    val amount: BigDecimal
)