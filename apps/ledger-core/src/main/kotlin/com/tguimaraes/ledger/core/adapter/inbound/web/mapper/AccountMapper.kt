package com.tguimaraes.ledger.core.adapter.inbound.web.mapper

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.CreateAccountRequest
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.CreateAccountResponse
import com.tguimaraes.ledger.core.application.dto.CreateAccountCommand
import com.tguimaraes.ledger.core.application.dto.CreateAccountResult

object AccountMapper {

    fun toCommand(request: CreateAccountRequest) =
        CreateAccountCommand(
            ownerName = request.ownerName
        )

    fun toResponse(result: CreateAccountResult) =
        CreateAccountResponse(
            accountId = result.accountId
        )
}
