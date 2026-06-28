package com.tguimaraes.ledger.core.adapter.outbound.persistence.repository

import com.tguimaraes.ledger.core.adapter.outbound.persistence.entity.OutboxJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface OutboxJpaRepository : JpaRepository<OutboxJpaEntity, UUID>