package com.tguimaraes.ledger.core.adapter.outbound.persistence.repository

import com.tguimaraes.ledger.core.adapter.outbound.persistence.entity.OutboxJpaEntity
import com.tguimaraes.ledger.core.domain.model.OutboxStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface OutboxJpaRepository : JpaRepository<OutboxJpaEntity, UUID> {
    fun findByStatus(status: OutboxStatus): List<OutboxJpaEntity>
}