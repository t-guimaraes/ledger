package com.tguimaraes.ledger.core.adapter.inbound.web.controller

import com.tguimaraes.ledger.core.adapter.inbound.web.doc.AccountApi
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.AccountStatementResponse
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.AccountBalanceResponse
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.AccountDepositRequest
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.AccountDepositResponse
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.CreateAccountRequest
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.CreateAccountResponse
import com.tguimaraes.ledger.core.adapter.inbound.web.mapper.AccountMapper
import com.tguimaraes.ledger.core.adapter.inbound.web.mapper.AccountBalanceMapper
import com.tguimaraes.ledger.core.adapter.inbound.web.mapper.AccountDepositMapper
import com.tguimaraes.ledger.core.adapter.inbound.web.mapper.AccountStatementMapper
import com.tguimaraes.ledger.core.application.port.input.CreateAccountDepositInputPort
import com.tguimaraes.ledger.core.application.port.input.CreateAccountInputPort
import com.tguimaraes.ledger.core.application.port.input.GetAccountBalanceInputPort
import com.tguimaraes.ledger.core.application.port.input.GetAccountStatementInputPort
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/accounts")
class AccountController(
    private val createAccountInputPort: CreateAccountInputPort,
    private val createAccountDepositInputPort: CreateAccountDepositInputPort,
    private val getAccountBalanceInputPort: GetAccountBalanceInputPort,
    private val getAccountStatementInputPort: GetAccountStatementInputPort
): AccountApi {

    @PostMapping
    override fun create(
        @Valid @RequestBody request: CreateAccountRequest
    ): ResponseEntity<CreateAccountResponse> {

        val result =
            createAccountInputPort.execute(
                AccountMapper.toCommand(request)
            )

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                AccountMapper.toResponse(result)
            )
    }

    @PostMapping("/{accountId}/deposit")
    override fun deposit(
        @PathVariable accountId: UUID,
        @RequestHeader("Idempotency-Key") idempotencyKey: String,
        @Valid @RequestBody request: AccountDepositRequest
    ): ResponseEntity<AccountDepositResponse> {

        val result = createAccountDepositInputPort.deposit(
            AccountDepositMapper.toCommand(request),
            accountId,
            idempotencyKey
        )

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                AccountDepositMapper.toResponse(result)
            )
    }

    @GetMapping("/{accountId}/balance")
    override fun getBalance(
        @PathVariable accountId: UUID
    ): AccountBalanceResponse {
        return AccountBalanceMapper.toResponse(getAccountBalanceInputPort.execute(accountId))
    }

    @GetMapping("/{accountId}/statement")
    override fun getStatement(
        @PathVariable accountId: UUID
    ): AccountStatementResponse {
        return AccountStatementMapper.toResponse(getAccountStatementInputPort.execute(accountId))
    }
}
