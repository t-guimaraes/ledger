package com.tguimaraes.ledger.core.domain.service

import com.tguimaraes.ledger.core.domain.exception.InsufficientBalanceException
import java.math.BigDecimal

class TransferDomainService {

    fun validateBalance(
        balance: BigDecimal,
        transferAmount: BigDecimal
    ) {
        if (balance < transferAmount) {
            throw InsufficientBalanceException()
        }
    }
}