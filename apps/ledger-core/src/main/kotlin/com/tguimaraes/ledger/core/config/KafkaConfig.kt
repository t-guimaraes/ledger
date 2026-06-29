package com.tguimaraes.ledger.core.config

import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.common.TopicPartition
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.util.backoff.FixedBackOff

@Configuration
class KafkaConfig(
    @Value("\${ledger.events.topic:ledger-events}")
    val ledgerEventsTopic: String,

    @Value("\${ledger.events.topic-dlq:ledger-events-dlq}")
    val ledgerEventsDlqTopic: String
) {

    @Bean
    fun topic(): NewTopic {
        return TopicBuilder.name(ledgerEventsTopic)
            .partitions(1)
            .replicas(1)
            .build()
    }

    @Bean
    fun dlqTopic(): NewTopic {
        return TopicBuilder.name(ledgerEventsDlqTopic)
            .partitions(1)
            .replicas(1)
            .build()
    }

    @Bean("commonErrorHandler")
    fun errorHandler(template: KafkaTemplate<Any, Any>): DefaultErrorHandler {
        // Configura o recuperador para usar SEMPRE o nome do tópico que você definiu no YAML
        val recoverer = DeadLetterPublishingRecoverer(template) { _, _ ->
            TopicPartition(ledgerEventsDlqTopic, 0)
        }

        val backOff = FixedBackOff(500L, 2)
        return DefaultErrorHandler(recoverer, backOff)
    }
}