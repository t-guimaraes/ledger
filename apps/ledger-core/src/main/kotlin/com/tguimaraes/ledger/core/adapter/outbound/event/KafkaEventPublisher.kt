package com.tguimaraes.ledger.core.adapter.outbound.event

import com.tguimaraes.ledger.core.application.port.output.event.EventPublisherPort
import com.tguimaraes.ledger.core.domain.event.DomainEvent
import com.tguimaraes.ledger.core.domain.event.EventEnvelope
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) : EventPublisherPort {

    override fun publish(event: DomainEvent) {

        val envelope = EventEnvelope(
            type = event::class.simpleName!!,
            data = event
        )

        kafkaTemplate.send(
            "ledger-events",
            envelope
        )
    }
}