package com.tguimaraes.ledger.core.adapter.outbound.id

import com.tguimaraes.ledger.core.application.port.output.id.IdGeneratorPort
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UuidGeneratorAdapter : IdGeneratorPort {

    override fun generate(): UUID =
        UUID.randomUUID()
}
