package com.ledger.core.application.port.output

import com.ledger.core.domain.model.Entry

interface EntryRepositoryPort {

    fun saveAll(entries: List<Entry>)
}