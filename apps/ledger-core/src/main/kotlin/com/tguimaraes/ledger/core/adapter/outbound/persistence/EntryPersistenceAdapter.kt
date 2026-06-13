package com.tguimaraes.ledger.core.adapter.outbound.persistence

import com.tguimaraes.ledger.core.adapter.outbound.persistence.mapper.EntryPersistenceMapper
import com.tguimaraes.ledger.core.adapter.outbound.persistence.repository.EntryJpaRepository
import com.tguimaraes.ledger.core.application.port.output.EntryRepositoryPort
import com.tguimaraes.ledger.core.domain.model.Entry
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.UUID

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