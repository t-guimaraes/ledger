package com.tguimaraes.ledger.core.application.port.output.id

import java.util.UUID

interface IdGeneratorPort {

    fun generate(): UUID
}
