package com.tguimaraes.ledger.core.adapter.outbound.persistence.repository

import com.tguimaraes.ledger.core.adapter.outbound.persistence.entity.IdempotencyKeyJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface IdempotencyKeyJpaRepository :
    JpaRepository<IdempotencyKeyJpaEntity, String>
