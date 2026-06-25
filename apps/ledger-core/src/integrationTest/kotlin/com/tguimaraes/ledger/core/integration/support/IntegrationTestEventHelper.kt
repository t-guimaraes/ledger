package com.tguimaraes.ledger.core.integration.support

import com.fasterxml.jackson.databind.ObjectMapper

object IntegrationTestEventHelper {

    inline fun <reified T> awaitEvent(
        receivedEvents: MutableList<String>,
        objectMapper: ObjectMapper,
        crossinline awaitEvents: () -> Unit
    ): String {

        val expected = T::class.simpleName

        awaitEvents()

        return receivedEvents.first {
            objectMapper.readTree(it).get("type").asText() == expected
        }
    }
}