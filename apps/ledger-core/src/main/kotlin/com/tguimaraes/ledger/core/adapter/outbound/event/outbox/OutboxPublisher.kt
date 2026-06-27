package com.tguimaraes.ledger.core.adapter.outbound.event.outbox

import com.tguimaraes.ledger.core.adapter.outbound.event.KafkaEventPublisher
import com.tguimaraes.ledger.core.application.port.output.repository.OutboxRepositoryPort
import com.tguimaraes.ledger.core.domain.model.OutboxStatus
import org.springframework.stereotype.Component

@Component
class OutboxPublisher(
    private val repository: OutboxRepositoryPort,
    private val kafkaPublisher: KafkaEventPublisher
) {

    fun publishPendingEvents() {

        val events = repository.findByStatus(OutboxStatus.PENDING)

        events.forEach { event ->

            try {
                kafkaPublisher.publish(event)
                repository.markPublished(event.id.toString())

            } catch (ex: Exception) {
                repository.markFailed(event.id.toString())
            }
        }
    }
}