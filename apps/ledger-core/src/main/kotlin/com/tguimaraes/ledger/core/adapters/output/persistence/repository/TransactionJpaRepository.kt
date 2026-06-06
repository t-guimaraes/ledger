package com.tguimaraes.ledger.core.adapters.output.persistence.repository

import com.tguimaraes.ledger.core.adapters.output.persistence.entity.TransactionJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TransactionJpaRepository :
    JpaRepository<TransactionJpaEntity, UUID>