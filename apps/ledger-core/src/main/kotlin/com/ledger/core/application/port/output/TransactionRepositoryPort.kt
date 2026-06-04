package com.ledger.core.application.port.output

import com.ledger.core.domain.model.Transaction

interface TransactionRepositoryPort {

    fun save(transaction: Transaction)
}