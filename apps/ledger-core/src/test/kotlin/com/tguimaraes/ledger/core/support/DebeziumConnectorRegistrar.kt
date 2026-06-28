package com.tguimaraes.ledger.core.support

import io.debezium.testing.testcontainers.DebeziumContainer
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

object DebeziumConnectorRegistrar {

    private val client = HttpClient.newHttpClient()

    fun register(container: DebeziumContainer, database: String, user: String, password: String) {
        val resource = DebeziumConnectorRegistrar::class.java
            .getResource("/outbox/debezium-outbox-test.json")
            ?.readText()
            ?: error("File debezium-outbox-test.json not found.")

        val json = resource
            .replace("\${DATABASE}", database)
            .replace("\${USER}", user)
            .replace("\${PASSWORD}", password)

        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:${container.getMappedPort(8083)}/connectors"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        require(response.statusCode() == 201 || response.statusCode() == 409) {
            "Connector error registering: ${response.statusCode()} - ${response.body()}"
        }
    }
}