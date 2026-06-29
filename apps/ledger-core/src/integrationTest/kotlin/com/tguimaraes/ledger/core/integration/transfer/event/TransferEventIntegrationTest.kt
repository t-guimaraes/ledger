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

    @KafkaListener(
        topics = ["#{@kafkaConfig.ledgerEventsTopic}"],
        groupId = "integration-test-transfer-event")
    fun listen(@Payload message: String, @Header("eventType") eventType: String) {
        receivedMessages.add(KafkaTestMessage(message, eventType))
    }

    @BeforeEach
    fun setup() {
        cleanDatabase()
        receivedMessages.clear()
    }

    @Test
    fun `should receive transfer event triggered by Debezium from the outbox`() {
        fromAccountId = UUID.randomUUID().also {
            createAccount(it, "Thiago")
            fundAccount(it, BigDecimal("1000.00"))
        }
        toAccountId = UUID.randomUUID().also {
            createAccount(it, "Maria")
        }

        val command = TransferCommand(fromAccountId, toAccountId, BigDecimal("200.00"))
        transferInputPort.transfer(command,"integration-key")

        val eventType = "TransferEvent"
        awaitEvents(eventType, fromAccountId.toString())

        val message = receivedMessages.last { it.type == eventType }.payload
        val event: EventEnvelope<TransferEvent> = objectMapper.readValue(message)

        assertThat(event.data.fromAccountId).isEqualTo(fromAccountId)
        assertThat(event.data.toAccountId).isEqualTo(toAccountId)
        assertThat(event.data.amount).isEqualByComparingTo(BigDecimal("200.00"))

        assertThat(outboxEventRepository.count()).isEqualTo(1)
    }
}