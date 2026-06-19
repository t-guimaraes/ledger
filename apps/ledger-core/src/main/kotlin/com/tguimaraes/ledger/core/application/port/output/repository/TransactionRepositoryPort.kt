package com.tguimaraes.ledger.core.application.port.output.repository

import com.tguimaraes.ledger.core.domain.model.Transaction

interface TransactionRepositoryPort {

    fun save(transaction: Transaction)
}