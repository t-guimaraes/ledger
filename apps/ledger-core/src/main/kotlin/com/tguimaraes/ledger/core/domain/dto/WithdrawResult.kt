package com.tguimaraes.ledger.core.domain.dto

import com.tguimaraes.ledger.core.domain.model.Entry
import com.tguimaraes.ledger.core.domain.model.Transaction

data class WithdrawResult(
    val transaction: Transaction,
    val entries: List<Entry>
)