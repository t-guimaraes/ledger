package com.tguimaraes.ledger.core.adapter.outbound.event

import com.tguimaraes.ledger.core.application.port.output.event.EventPublisherPort
import com.tguimaraes.ledger.core.domain.event.outbox.OutboxEvent
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) : EventPublisherPort {

    override fun publish(event: OutboxEvent) {
        kafkaTemplate.send(
            "ledger-events",
            event.payload
        ).get()
    }
}