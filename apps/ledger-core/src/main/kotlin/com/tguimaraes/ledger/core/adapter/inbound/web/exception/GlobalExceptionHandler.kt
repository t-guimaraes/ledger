package com.tguimaraes.ledger.core.adapter.inbound.web.exception

import com.tguimaraes.ledger.core.adapter.inbound.web.dto.ApiErrorResponse
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.FieldErrorResponse
import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import com.tguimaraes.ledger.core.domain.exception.IdempotencyException
import com.tguimaraes.ledger.core.domain.exception.InsufficientBalanceException
import com.tguimaraes.ledger.core.domain.exception.InvalidAccountOwnerNameException
import com.tguimaraes.ledger.core.domain.exception.InvalidTransferAmountException
import com.tguimaraes.ledger.core.domain.exception.SameAccountTransferException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.logging.Level
import java.util.logging.Logger

@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = Logger.getLogger(GlobalExceptionHandler::class.java.name)

    @ExceptionHandler(
        InvalidTransferAmountException::class,
        SameAccountTransferException::class
    )
    fun handleBadRequest(ex: RuntimeException): ResponseEntity<ApiErrorResponse> =
        build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.message)

    @ExceptionHandler(AccountNotFoundException::class)
    fun handleNotFound(ex: AccountNotFoundException): ResponseEntity<ApiErrorResponse> =
        build(HttpStatus.NOT_FOUND, "ACCOUNT_NOT_FOUND", ex.message)

    @ExceptionHandler(InsufficientBalanceException::class)
    fun handleBusiness(ex: InsufficientBalanceException): ResponseEntity<ApiErrorResponse> =
        build(HttpStatus.UNPROCESSABLE_ENTITY, "INSUFFICIENT_BALANCE", ex.message)

    @ExceptionHandler(IdempotencyException::class)
    fun handleConflict(ex: IdempotencyException): ResponseEntity<ApiErrorResponse> =
        build(HttpStatus.CONFLICT, "IDEMPOTENCY_CONFLICT", ex.message)

    @ExceptionHandler(InvalidAccountOwnerNameException::class)
    fun handleInvalidAccount(ex: InvalidAccountOwnerNameException): ResponseEntity<ApiErrorResponse> =
        build(HttpStatus.BAD_REQUEST, "INVALID_ACCOUNT_OWNER_NAME", ex.message)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ApiErrorResponse> {

        val errors = ex.bindingResult.fieldErrors.map {
            FieldErrorResponse(
                field = it.field,
                message = it.defaultMessage ?: "Invalid value"
            )
        }

        val message = ex.message ?: "Unexpected error"
        logger.log(Level.SEVERE,"Unhandled exception", message)

        return build(
            HttpStatus.BAD_REQUEST,
            "VALIDATION_ERROR",
            "Validation failed",
            errors
        )
    }

    private fun build(
        status: HttpStatus,
        code: String,
        message: String?,
        errors: List<FieldErrorResponse> = emptyList()
    ): ResponseEntity<ApiErrorResponse> {

        return ResponseEntity.status(status).body(
            ApiErrorResponse(
                status = status.value(),
                code = code,
                message = message ?: status.reasonPhrase,
                errors = errors
            )
        )
    }
}
