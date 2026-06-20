package com.tguimaraes.ledger.core.adapter.inbound.web.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class CreateAccountRequest(

    @field:Schema(example = "Thiago Henrique")
    @field:NotBlank(message = "Owner name must not be blank")
    val ownerName: String
)