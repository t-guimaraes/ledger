package com.tguimaraes.ledger.core.adapter.inbound.web.exception

import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import com.tguimaraes.ledger.core.domain.exception.IdempotencyException
import com.tguimaraes.ledger.core.domain.exception.InsufficientBalanceException
import com.tguimaraes.ledger.core.domain.exception.InvalidTransferAmountException
import com.tguimaraes.ledger.core.domain.exception.SameAccountTransferException
import com.tguimaraes.ledger.core.support.TestFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException

class GlobalExceptionHandlerTest {

    private val handler = GlobalExceptionHandler()

    @Test
    fun `should return bad request for invalid transfer amount`() {

        val result = handler.handleBadRequest(
            InvalidTransferAmountException()
        )

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.statusCode.value())
        assertEquals("BAD_REQUEST", result.body?.code)
        assertEquals("Transfer amount must be greater than zero", result.body?.message)
    }

    @Test
    fun `should return bad request for same account transfer`() {

        val result = handler.handleBadRequest(
            SameAccountTransferException()
        )

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.statusCode.value())
        assertEquals("BAD_REQUEST", result.body?.code)
        assertEquals("Source and destination accounts must be different", result.body?.message)
    }

    @Test
    fun `should return not found for account not found`() {

        val result = handler.handleNotFound(
            AccountNotFoundException(TestFixtures.FROM_ACCOUNT_ID)
        )

        assertEquals(HttpStatus.NOT_FOUND.value(), result.statusCode.value())
        assertEquals("ACCOUNT_NOT_FOUND", result.body?.code)
        assertEquals(
            "Account ${TestFixtures.FROM_ACCOUNT_ID} not found",
            result.body?.message
        )
    }

    @Test
    fun `should return unprocessable entity for insufficient balance`() {

        val result = handler.handleBusiness(
            InsufficientBalanceException()
        )

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.statusCode.value())
        assertEquals("INSUFFICIENT_BALANCE", result.body?.code)
        assertEquals("Insufficient balance", result.body?.message)
    }

    @Test
    fun `should return conflict for idempotency exception`() {

        val result = handler.handleConflict(
            IdempotencyException("Request already processed")
        )

        assertEquals(HttpStatus.CONFLICT.value(), result.statusCode.value())
        assertEquals("IDEMPOTENCY_CONFLICT", result.body?.code)
        assertEquals("Request already processed", result.body?.message)
    }

    @Test
    fun `should handle validation errors`() {
        val method = Any::class.java.getDeclaredMethod("toString")
        val methodParameter = MethodParameter(method, -1)

        val bindingResult = BeanPropertyBindingResult(
            Any(),
            "object"
        )

        bindingResult.addError(
            FieldError("object", "ownerName", "must not be blank")
        )

        val ex = MethodArgumentNotValidException(
            methodParameter,
            bindingResult
        )

        val result = handler.handleValidation(ex)

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.statusCode.value())
        assertEquals("VALIDATION_ERROR", result.body?.code)
        assertEquals("Validation failed", result.body?.message)

        assertEquals(1, result.body?.errors?.size)
        assertEquals("ownerName", result.body?.errors?.first()?.field)
        assertEquals("must not be blank", result.body?.errors?.first()?.message)
    }
}