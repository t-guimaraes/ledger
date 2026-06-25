package com.tguimaraes.ledger.core.integration.transfer.event

import com.fasterxml.jackson.module.kotlin.readValue
import com.tguimaraes.ledger.core.adapter.inbound.web.dto.transfer.TransferRequest
import com.tguimaraes.ledger.core.domain.event.EventEnvelope
import com.tguimaraes.ledger.core.domain.event.transfer.TransferEvent
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

class TransferKafkaIntegrationTest : AbstractIntegrationTest() {

    protected val receivedEvents = CopyOnWriteArrayList<String>()

    @KafkaListener(topics = ["ledger-events"], groupId = "test-group-transfer-events")
    fun listen(message: String) {
        receivedEvents.add(message)
    }

    @BeforeEach
    fun setup() {
        receivedEvents.clear()
    }

    @Test
    fun `should publish TransferEvent when transfer is executed`() {
        val fromAccountId = UUID.randomUUID()
        createAccount(fromAccountId, "Thiago")
        fundAccount(fromAccountId, BigDecimal("1000.00"))

        val toAccountId = UUID.randomUUID()
        createAccount(toAccountId, "Maria")

        val request =
            TransferRequest(
                fromAccountId = fromAccountId,
                toAccountId = toAccountId,
                amount = BigDecimal("200.00")
            )

        RestAssured.given()
            .port(port)
            .contentType("application/json")
            .header("Idempotency-Key", "integration-key")
            .body(request)
            .log().all()
            .post("/transfers")
            .then()
            .log().all()
            .statusCode(201)

        val json = IntegrationTestEventHelper.awaitEvent<TransferEvent>(
            receivedEvents,
            objectMapper
        ) {
            Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .until {
                    receivedEvents.isNotEmpty()
                }
        }
        val envelope: EventEnvelope<TransferEvent> = objectMapper.readValue(json)

        Assertions.assertThat(envelope).isNotNull
        Assertions.assertThat(envelope.type).isEqualTo("TransferEvent")
        Assertions.assertThat(envelope.version).isEqualTo(1)

        Assertions.assertThat(envelope.data.fromAccountId).isEqualTo(fromAccountId)
        Assertions.assertThat(envelope.data.toAccountId).isEqualTo(toAccountId)
        Assertions.assertThat(envelope.data.amount)
            .isEqualByComparingTo(BigDecimal("200.00"))
    }
}