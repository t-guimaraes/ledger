package com.tguimaraes.ledger.core.adapter.inbound.web.dto.account

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class AccountStatementResponse(

    @field:Schema(example = "11111111-1111-1111-1111-111111111111")
    val accountId: UUID,

    val entries: List<AccountStatementEntryResponse>
)