package com.ledger.core.adapters.output.persistence.repository

import com.ledger.core.adapters.output.persistence.entity.TransactionJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TransactionJpaRepository :
    JpaRepository<TransactionJpaEntity, UUID>