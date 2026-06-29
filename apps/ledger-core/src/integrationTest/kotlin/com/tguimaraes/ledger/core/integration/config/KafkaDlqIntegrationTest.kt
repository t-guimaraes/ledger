package com.tguimaraes.ledger.core.integration.config

import com.fasterxml.jackson.module.kotlin.readValue
import com.tguimaraes.ledger.core.integration.support.AbstractIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.messaging.handler.annotation.Payload
import java.time.Duration

class KafkaDlqIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    @KafkaListener(
        topics = ["#{@kafkaConfig.ledgerEventsTopic}"],
        groupId = "integration-test-faulty-group"
    )
    fun listenAndFail(@Payload message: String) {
        throw RuntimeException("Forced failure to test the DLQ flow")
    }

    @KafkaListener(
        topics = ["#{@kafkaConfig.ledgerEventsDlqTopic}"],
        groupId = "integration-test-dlq-consumer-group")
    fun listenDlq(@Payload message: String) {
        receivedMessages.add(KafkaTestMessage(message, "DLQ_EVENT"))
    }

    @BeforeEach
    fun setup() {
        cleanDatabase()
        receivedMessages.clear()
    }

    @Test
    fun `should route message to DLQ when listener throws exception`() {
        val payload = mapOf("id" to "123", "status" to "FAILING")

        kafkaTemplate.send(kafkaConfig.ledgerEventsTopic, payload)

        Awaitility.await().atMost(Duration.ofSeconds(5)).until {
            receivedMessages.any { it.type == "DLQ_EVENT" }
        }

        val dlqMessage = receivedMessages.first { it.type == "DLQ_EVENT" }.payload
        val json = objectMapper.readTree(dlqMessage).asText()
        val actualPayload: Map<String, String> = objectMapper.readValue(json)

        assertThat(actualPayload).isEqualTo(payload)
    }
}