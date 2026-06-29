package com.tguimaraes.ledger.core.integration.account.event

import com.fasterxml.jackson.module.kotlin.readValue
import com.tguimaraes.ledger.core.application.dto.account.AccountDepositCommand
import com.tguimaraes.ledger.core.application.port.input.AccountDepositInputPort
import com.tguimaraes.ledger.core.domain.event.EventEnvelope
import com.tguimaraes.ledger.core.domain.event.account.AccountDepositEvent
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

class AccountDepositEventIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var accountDepositInputPort: AccountDepositInputPort

    @KafkaListener(
        topics = ["#{@kafkaConfig.ledgerEventsTopic}"],
        groupId = "integration-test-account-deposit-event")
    fun listen(@Payload message: String, @Header("eventType") eventType: String) {
        receivedMessages.add(KafkaTestMessage(message, eventType))
    }

    @BeforeEach
    fun setup() {
        cleanDatabase()
        receivedMessages.clear()
    }

    @Test
    fun `should receive account deposit event triggered by Debezium from the outbox`() {
        fromAccountId = UUID.randomUUID().also {
            createAccount(it, "Thiago")
        }

        val command = AccountDepositCommand(BigDecimal("1000.00"))
        accountDepositInputPort.deposit(command, fromAccountId,"integration-key")

        val eventType = "AccountDepositEvent"
        awaitEvents(eventType, fromAccountId.toString())

        val message = receivedMessages.last { it.type == eventType }.payload
        val event: EventEnvelope<AccountDepositEvent> = objectMapper.readValue(message)

        assertThat(event.data.accountId).isEqualTo(fromAccountId)
        assertThat(event.data.amount).isEqualByComparingTo(BigDecimal("1000.00"))

        assertThat(outboxEventRepository.count()).isEqualTo(1)
    }
}