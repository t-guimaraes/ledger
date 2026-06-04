package com.ledger.core.application.port.output

import com.ledger.core.domain.model.Account
import java.util.UUID

interface AccountRepositoryPort {

    fun findById(id: UUID): Account?

    fun save(account: Account)
}