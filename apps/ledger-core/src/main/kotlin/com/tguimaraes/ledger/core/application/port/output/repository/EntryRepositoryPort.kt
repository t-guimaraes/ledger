package com.tguimaraes.ledger.core.application.port.output.repository

import com.tguimaraes.ledger.core.domain.model.Entry

interface EntryRepositoryPort {

    fun saveAll(entries: List<Entry>)
}