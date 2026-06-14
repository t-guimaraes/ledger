package com.tguimaraes.ledger.core.adapter.inbound.web.exception

import com.tguimaraes.ledger.core.domain.exception.AccountNotFoundException
import com.tguimaraes.ledger.core.domain.exception.IdempotencyException
import com.tguimaraes.ledger.core.domain.exception.InsufficientBalanceException
import com.tguimaraes.ledger.core.domain.exception.InvalidTransferAmountException
import com.tguimaraes.ledger.core.domain.exception.SameAccountTransferException
import com.tguimaraes.ledger.core.support.TestFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class GlobalExceptionHandlerTest {

    private val handler = GlobalExceptionHandler()

    @Test
    fun `should return bad request for invalid transfer amount`() {

        val result = handler.handleBadRequest(
            InvalidTransferAmountException()
        )

        assertEquals(
            HttpStatus.BAD_REQUEST.value(),
            result.status
        )

        assertEquals(
            "Transfer amount must be greater than zero",
            result.detail
        )
    }

    @Test
    fun `should return bad request for same account transfer`() {

        val result = handler.handleBadRequest(
            SameAccountTransferException()
        )

        assertEquals(
            HttpStatus.BAD_REQUEST.value(),
            result.status
        )

        assertEquals(
            "Source and destination accounts must be different",
            result.detail
        )
    }

    @Test
    fun `should return not found for account not found`() {

        val result = handler.handleAccountNotFound(
            AccountNotFoundException(
                TestFixtures.FROM_ACCOUNT_ID
            )
        )

        assertEquals(
            HttpStatus.NOT_FOUND.value(),
            result.status
        )

        assertEquals(
            "Account ${TestFixtures.FROM_ACCOUNT_ID} not found",
            result.detail
        )
    }

    @Test
    fun `should return unprocessable entity for insufficient balance`() {

        val result = handler.handleInsufficientBalance(
            InsufficientBalanceException()
        )

        assertEquals(
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            result.status
        )

        assertEquals(
            "Insufficient balance",
            result.detail
        )
    }

    @Test
    fun `should return conflict for idempotency exception`() {

        val result = handler.handleIdempotency(
            IdempotencyException("Request already processed")
        )

        assertEquals(
            HttpStatus.CONFLICT.value(),
            result.status
        )

        assertEquals(
            "Request already processed",
            result.detail
        )
    }

    @Test
    fun `should use reason phrase when exception message is null`() {

        val result = handler.handleBadRequest(
            object : RuntimeException() {}
        )

        assertEquals(
            HttpStatus.BAD_REQUEST.value(),
            result.status
        )

        assertEquals(
            HttpStatus.BAD_REQUEST.reasonPhrase,
            result.detail
        )
    }
}