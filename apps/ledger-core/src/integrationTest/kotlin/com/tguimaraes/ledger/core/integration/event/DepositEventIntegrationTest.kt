package com.tguimaraes.ledger.core.integration.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.tguimaraes.ledger.core.domain.event.account.AccountDepositedEvent
import com.tguimaraes.ledger.core.domain.event.EventEnvelope
import com.tguimaraes.ledger.core.integration.support.AbstractIntegrationTest
import io.restassured.RestAssured
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigDecimal
import java.time.Duration
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class DepositEventIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, String>

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @LocalServerPort
    var port: Int = 8080

    private val receivedEvents = CopyOnWriteArrayList<String>()

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun kafkaProps(registry: DynamicPropertyRegistry) {
            registry.add("spring.kafka.bootstrap-servers") {
                kafkaContainer.bootstrapServers
            }
        }
    }

    @BeforeEach
    fun setup() {
        receivedEvents.clear()
    }

    @KafkaListener(topics = ["ledger-events"], groupId = "test-group")
    fun listen(message: String) {
        receivedEvents.add(message)
    }

    @Test
    fun `should publish AccountDepositedEvent when deposit is executed`() {

        val accountId = createAccount()

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

        await()
            .atMost(Duration.ofSeconds(10))
            .until {
                receivedEvents.isNotEmpty()
            }

        val envelope: EventEnvelope<AccountDepositedEvent> =
            objectMapper.readValue(receivedEvents.first())

        assertThat(envelope).isNotNull
        assertThat(envelope.type).isEqualTo("AccountDepositedEvent")
        assertThat(envelope.version).isEqualTo(1)

        assertThat(envelope.data.accountId).isEqualTo(accountId)
        assertThat(envelope.data.amount)
            .isEqualByComparingTo(BigDecimal("100.00"))
    }

    private fun createAccount(): UUID {

        val response = RestAssured.given()
            .port(port)
            .contentType("application/json")
            .body(
                """
                {
                  "ownerName": "Thiago New Account"
                }
                """.trimIndent()
            )
            .post("/accounts")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()

        return UUID.fromString(response.getString("accountId"))
    }
}