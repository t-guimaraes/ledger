package com.tguimaraes.ledger.core.adapter.outbound.persistence.repository

import com.tguimaraes.ledger.core.adapter.outbound.persistence.entity.EntryJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.math.BigDecimal
import java.util.*

interface EntryJpaRepository :
    JpaRepository<EntryJpaEntity, UUID> {

    @Query("""
        SELECT COALESCE(
            SUM(
                CASE
                    WHEN e.type = 'CREDIT' THEN e.amount
                    WHEN e.type = 'DEBIT' THEN -e.amount
                    ELSE 0
                END
            ),
            0
        )
        FROM EntryJpaEntity e
        WHERE e.accountId = :accountId
    """)
    fun getBalance(accountId: UUID): BigDecimal

    fun findAllByAccountIdOrderByCreatedAtDesc(
        accountId: UUID
    ): List<EntryJpaEntity>
}