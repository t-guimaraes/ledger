package com.tguimaraes.ledger.core.adapter.inbound.web.exception

import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import com.tguimaraes.ledger.core.domain.exception.IdempotencyException
import com.tguimaraes.ledger.core.domain.exception.InsufficientBalanceException
import com.tguimaraes.ledger.core.domain.exception.InvalidTransferAmountException
import com.tguimaraes.ledger.core.domain.exception.SameAccountTransferException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(InvalidTransferAmountException::class, SameAccountTransferException::class)
    fun handleBadRequest(exception: RuntimeException): ProblemDetail =
        problemDetail(HttpStatus.BAD_REQUEST, exception)

    @ExceptionHandler(AccountNotFoundException::class)
    fun handleAccountNotFound(exception: AccountNotFoundException): ProblemDetail =
        problemDetail(HttpStatus.NOT_FOUND, exception)

    @ExceptionHandler(InsufficientBalanceException::class)
    fun handleInsufficientBalance(exception: InsufficientBalanceException): ProblemDetail =
        problemDetail(HttpStatus.UNPROCESSABLE_ENTITY, exception)

    @ExceptionHandler(IdempotencyException::class)
    fun handleIdempotency(exception: IdempotencyException): ProblemDetail =
        problemDetail(HttpStatus.CONFLICT, exception)

    private fun problemDetail(
        status: HttpStatus,
        exception: RuntimeException
    ): ProblemDetail =
        ProblemDetail.forStatusAndDetail(
            status,
            exception.message ?: status.reasonPhrase
        )
}
