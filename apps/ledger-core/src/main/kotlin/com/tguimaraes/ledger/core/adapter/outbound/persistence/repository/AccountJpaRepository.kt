package com.tguimaraes.ledger.core.adapter.outbound.persistence.repository

import com.tguimaraes.ledger.core.adapter.outbound.persistence.entity.AccountJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AccountJpaRepository :
    JpaRepository<AccountJpaEntity, UUID>