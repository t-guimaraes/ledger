package com.tguimaraes.ledger.core.application.port.output.repository

import com.tguimaraes.ledger.core.domain.model.Account
import java.util.UUID

interface AccountRepositoryPort {

    fun findById(id: UUID): Account?

    fun save(account: Account)
}