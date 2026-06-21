package com.tguimaraes.ledger.core.adapter.inbound.web.mapper.account

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.account.CreateAccountRequest
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.account.CreateAccountResponse
import com.tguimaraes.ledger.core.application.dto.account.CreateAccountCommand
import com.tguimaraes.ledger.core.application.dto.account.CreateAccountResult

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
