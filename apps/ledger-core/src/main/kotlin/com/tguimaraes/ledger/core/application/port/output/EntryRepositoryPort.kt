package com.tguimaraes.ledger.core.application.port.output

import com.tguimaraes.ledger.core.domain.model.Entry
import java.math.BigDecimal
import java.util.UUID

interface EntryRepositoryPort {

    fun saveAll(entries: List<Entry>)

    fun getBalance(accountId: UUID): BigDecimal
}