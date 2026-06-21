package com.tguimaraes.ledger.core.adapter.inbound.web.doc

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.AccountStatementResponse
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.AccountBalanceResponse
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.CreateAccountDepositRequest
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.CreateAccountDepositResponse
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.CreateAccountRequest
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.CreateAccountResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import java.util.UUID

@Tag(name = "Accounts")
interface AccountApi {

    @Operation(
        summary = "Create an account",
        description = "Create an account with an account name."
    )
    fun create(
        @RequestBody(
            required = true,
            description = "Account creation payload"
        )
        request: CreateAccountRequest,
    ): ResponseEntity<CreateAccountResponse>

    @Operation(
        summary = "Deposit an account",
        description = "Deposit money in account."
    )
    fun deposit(
        @Parameter(
            description = "UUID of the account",
            example = "11111111-1111-1111-1111-111111111111"
        )
        accountId: UUID,
        @Parameter(
            description = "Unique key used to guarantee idempotent requests",
            example = "Idempotency-Key",
            required = true
        )
        idempotencyKey: String,
        @RequestBody(
            required = true,
            description = "Account deposit payload"
        )
        request: CreateAccountDepositRequest,
    ): ResponseEntity<CreateAccountDepositResponse>

    @Operation(
        summary = "Get account balance",
        description = "Returns current account balance"
    )
    fun getBalance(
        @Parameter(
            description = "UUID of the account",
            example = "11111111-1111-1111-1111-111111111111"
        )
        accountId: UUID,
    ): AccountBalanceResponse

    @Operation(
        summary = "Get account statement",
        description = "Returns current account statement"
    )
    fun getStatement(
        @Parameter(
            description = "UUID of the account",
            example = "11111111-1111-1111-1111-111111111111"
        )
        accountId: UUID,
    ): AccountStatementResponse
}