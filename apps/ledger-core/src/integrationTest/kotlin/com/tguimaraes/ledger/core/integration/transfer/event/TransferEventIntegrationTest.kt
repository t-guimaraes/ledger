package com.tguimaraes.ledger.core.integration.transfer.event

import com.fasterxml.jackson.module.kotlin.readValue
import com.tguimaraes.ledger.core.application.dto.transfer.TransferCommand
import com.tguimaraes.ledger.core.application.port.input.TransferInputPort
import com.tguimaraes.ledger.core.domain.event.EventEnvelope
import com.tguimaraes.ledger.core.domain.event.transfer.TransferEvent
import com.tguimaraes.ledger.core.integration.support.AbstractIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import java.math.BigDecimal
import java.util.*

class TransferEventIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var transferInputPort: TransferInputPort

    @KafkaListener(topics = ["ledger-events"], groupId = "integration-test-transfer-event")
    fun listen(@Payload message: String, @Header("eventType") eventType: String) {
        receivedEvents.add(message)
        receivedEventTypes.add(eventType)
    }

    @BeforeEach
    fun setup() {
        cleanDatabase()
        receivedEvents.clear()
        receivedEventTypes.clear()
    }

    @Test
    fun `should receive transfer event by kafka`() {
        val fromId = UUID.randomUUID().also {
            createAccount(it, "Thiago"); fundAccount(it, BigDecimal("1000.00"))
        }
        val toId = UUID.randomUUID().also {
            createAccount(it, "Maria")
        }

        transferInputPort.transfer(
            TransferCommand(fromId,toId, BigDecimal("200.00")),
            "integration-key"
        )

        awaitEvents()

        assertThat(receivedEventTypes.first()).isEqualTo("TransferEvent")

        val json = receivedEvents.first { objectMapper.readTree(it).get("type").asText() == "TransferEvent" }
        val event: EventEnvelope<TransferEvent> = objectMapper.readValue(json)

        assertThat(event.data.fromAccountId).isEqualTo(fromId)
        assertThat(event.data.toAccountId).isEqualTo(toId)
        assertThat(event.data.amount).isEqualByComparingTo(BigDecimal("200.00"))
    }
}