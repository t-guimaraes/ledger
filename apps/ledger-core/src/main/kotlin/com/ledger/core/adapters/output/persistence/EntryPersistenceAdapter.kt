package com.ledger.core.adapters.output.persistence

import com.ledger.core.adapters.output.persistence.mapper.EntryPersistenceMapper
import com.ledger.core.adapters.output.persistence.repository.EntryJpaRepository
import com.ledger.core.application.port.output.EntryRepositoryPort
import com.ledger.core.domain.model.Entry
import org.springframework.stereotype.Component

@Component
class EntryPersistenceAdapter(
    private val repository: EntryJpaRepository
) : EntryRepositoryPort {

    override fun saveAll(entries: List<Entry>) {
        repository.saveAll(
            entries.map(EntryPersistenceMapper::toEntity)
        )
    }
}