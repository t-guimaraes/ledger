package com.tguimaraes.ledger.core.adapter.inbound.web.controller

import com.tguimaraes.ledger.core.adapter.inbound.web.doc.AccountApi
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.AccountStatementResponse
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.AccountBalanceResponse
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.StatementEntryResponse
import com.tguimaraes.ledger.core.adapter.inbound.web.mapper.AccountBalanceMapper
import com.tguimaraes.ledger.core.adapter.inbound.web.mapper.AccountStatementMapper
import com.tguimaraes.ledger.core.application.port.input.GetAccountBalanceInputPort
import com.tguimaraes.ledger.core.application.port.input.GetAccountStatementInputPort
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/accounts")
class AccountController(
    private val getAccountBalanceInputPort: GetAccountBalanceInputPort,
    private val getAccountStatementInputPort: GetAccountStatementInputPort
): AccountApi {

    @GetMapping("/{accountId}/balance")
    override fun getBalance(
        @Parameter(
            description = "UUID of the account",
            example = "11111111-1111-1111-1111-111111111111"
        )
        @PathVariable accountId: UUID
    ): AccountBalanceResponse {
        return AccountBalanceMapper.toResponse(getAccountBalanceInputPort.execute(accountId))
    }

    @GetMapping("/{accountId}/statement")
    override fun getStatement(
        @Parameter(
            description = "UUID of the account",
            example = "11111111-1111-1111-1111-111111111111"
        )
        @PathVariable accountId: UUID
    ): AccountStatementResponse {
        return AccountStatementMapper.toResponse(getAccountStatementInputPort.execute(accountId))
    }
}

