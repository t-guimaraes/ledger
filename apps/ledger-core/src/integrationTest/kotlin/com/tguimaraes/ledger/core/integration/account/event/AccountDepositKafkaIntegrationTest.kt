package com.tguimaraes.ledger.core.integration.account.event

import com.fasterxml.jackson.module.kotlin.readValue
import com.tguimaraes.ledger.core.domain.event.EventEnvelope
import com.tguimaraes.ledger.core.domain.event.account.AccountDepositEvent
import com.tguimaraes.ledger.core.integration.support.AbstractIntegrationTest
import com.tguimaraes.ledger.core.integration.support.IntegrationTestEventHelper
import io.restassured.RestAssured
import org.assertj.core.api.Assertions
import org.awaitility.Awaitility
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.kafka.annotation.KafkaListener
import java.math.BigDecimal
import java.time.Duration
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

class AccountDepositKafkaIntegrationTest : AbstractIntegrationTest() {

    protected val receivedEvents = CopyOnWriteArrayList<String>()

    @KafkaListener(topics = ["ledger-events"], groupId = "test-group-account-deposit-events")
    fun listen(message: String) {
        receivedEvents.add(message)
    }

    @BeforeEach
    fun setup() {
        receivedEvents.clear()
    }

    @Test
    fun `should publish AccountDepositEvent when deposit is executed`() {
        val accountId = UUID.randomUUID()
        createAccount(accountId, "Thiago Henrique")

        RestAssured.given()
            .port(port)
            .contentType("application/json")
            .header("Idempotency-Key", "integration-key")
            .body(
                """
                {
                  "amount": 100.00
                }
                """.trimIndent()
            )
            .post("/accounts/$accountId/deposit")
            .then()
            .statusCode(201)

        val json = IntegrationTestEventHelper.awaitEvent<AccountDepositEvent>(
            receivedEvents,
            objectMapper
        ) {
            Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .until {
                    receivedEvents.isNotEmpty()
                }
        }
        val envelope: EventEnvelope<AccountDepositEvent> = objectMapper.readValue(json)

        Assertions.assertThat(envelope).isNotNull
        Assertions.assertThat(envelope.type).isEqualTo("AccountDepositEvent")
        Assertions.assertThat(envelope.version).isEqualTo(1)

        Assertions.assertThat(envelope.data.accountId).isEqualTo(accountId)
        Assertions.assertThat(envelope.data.amount)
            .isEqualByComparingTo(BigDecimal("100.00"))
    }
}