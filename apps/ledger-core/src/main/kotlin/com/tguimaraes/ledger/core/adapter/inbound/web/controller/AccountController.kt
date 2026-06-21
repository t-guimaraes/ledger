package com.tguimaraes.ledger.core.adapter.inbound.web.controller

import com.tguimaraes.ledger.core.adapter.inbound.web.doc.AccountApiDoc
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.account.*
import com.tguimaraes.ledger.core.adapter.inbound.web.mapper.account.AccountBalanceMapper
import com.tguimaraes.ledger.core.adapter.inbound.web.mapper.account.AccountDepositMapper
import com.tguimaraes.ledger.core.adapter.inbound.web.mapper.account.AccountMapper
import com.tguimaraes.ledger.core.adapter.inbound.web.mapper.account.AccountStatementMapper
import com.tguimaraes.ledger.core.application.port.input.AccountBalanceInputPort
import com.tguimaraes.ledger.core.application.port.input.AccountDepositInputPort
import com.tguimaraes.ledger.core.application.port.input.AccountStatementInputPort
import com.tguimaraes.ledger.core.application.port.input.CreateAccountInputPort
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/accounts")
class AccountController(
    private val createAccountInputPort: CreateAccountInputPort,
    private val accountDepositInputPort: AccountDepositInputPort,
    private val accountBalanceInputPort: AccountBalanceInputPort,
    private val accountStatementInputPort: AccountStatementInputPort
): AccountApiDoc {

    @PostMapping
    override fun create(
        @Valid @RequestBody request: CreateAccountRequest
    ): ResponseEntity<CreateAccountResponse> {

        val result = createAccountInputPort.execute(AccountMapper.toCommand(request))

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(AccountMapper.toResponse(result))
    }

    @PostMapping("/{accountId}/deposit")
    override fun deposit(
        @PathVariable accountId: UUID,
        @RequestHeader("Idempotency-Key") idempotencyKey: String,
        @Valid @RequestBody request: AccountDepositRequest
    ): ResponseEntity<AccountDepositResponse> {

        val result = accountDepositInputPort.deposit(
            AccountDepositMapper.toCommand(request),
            accountId,
            idempotencyKey
        )

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(AccountDepositMapper.toResponse(result))
    }

    @GetMapping("/{accountId}/balance")
    override fun getBalance(
        @PathVariable accountId: UUID
    ): AccountBalanceResponse {
        return AccountBalanceMapper.toResponse(accountBalanceInputPort.execute(accountId))
    }

    @GetMapping("/{accountId}/statement")
    override fun getStatement(
        @PathVariable accountId: UUID
    ): AccountStatementResponse {
        return AccountStatementMapper.toResponse(accountStatementInputPort.execute(accountId))
    }
}
