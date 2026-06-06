package com.tguimaraes.ledger.core.adapters.input.web

import com.tguimaraes.ledger.core.application.usecase.GetAccountBalanceUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.util.UUID

@RestController
@RequestMapping("/accounts")
class AccountController(
    private val getAccountBalanceUseCase: GetAccountBalanceUseCase
) {

    @GetMapping("/{accountId}/balance")
    fun getBalance(
        @PathVariable accountId: UUID
    ): BalanceResponse {

        val balance =
            getAccountBalanceUseCase.execute(accountId)

        return BalanceResponse(
            accountId = accountId,
            balance = balance
        )
    }
}

data class BalanceResponse(
    val accountId: UUID,
    val balance: BigDecimal
)