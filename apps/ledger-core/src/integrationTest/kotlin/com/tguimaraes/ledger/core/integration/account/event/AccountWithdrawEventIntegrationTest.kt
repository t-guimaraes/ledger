package com.tguimaraes.ledger.core.integration.account.event

import com.fasterxml.jackson.module.kotlin.readValue
import com.tguimaraes.ledger.core.application.dto.account.AccountWithdrawCommand
import com.tguimaraes.ledger.core.application.port.input.AccountWithdrawInputPort
import com.tguimaraes.ledger.core.domain.event.EventEnvelope
import com.tguimaraes.ledger.core.domain.event.account.AccountWithdrawEvent
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

class AccountWithdrawEventIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var accountWithdrawInputPort: AccountWithdrawInputPort

    @KafkaListener(
        topics = ["#{@kafkaConfig.ledgerEventsTopic}"],
        groupId = "integration-test-account-withdraw-event")
    fun listen(@Payload message: String, @Header("eventType") eventType: String) {
        receivedMessages.add(KafkaTestMessage(message, eventType))
    }

    @BeforeEach
    fun setup() {
        cleanDatabase()
        receivedMessages.clear()
    }

    @Test
    fun `should receive account withdraw event triggered by Debezium from the outbox`() {
        fromAccountId = UUID.randomUUID().also {
            createAccount(it, "Thiago")
            fundAccount(it, BigDecimal("1000.00"))
        }

        val command = AccountWithdrawCommand(BigDecimal("200.00"))
        accountWithdrawInputPort.withdraw(command, fromAccountId,"integration-key")

        val eventType = "AccountWithdrawEvent"
        awaitEvents(eventType, fromAccountId.toString())

        val message = receivedMessages.last { it.type == eventType }.payload
        val event: EventEnvelope<AccountWithdrawEvent> = objectMapper.readValue(message)

        assertThat(event.data.accountId).isEqualTo(fromAccountId)
        assertThat(event.data.amount).isEqualByComparingTo(BigDecimal("200.00"))

        assertThat(outboxEventRepository.count()).isEqualTo(1)
    }
}