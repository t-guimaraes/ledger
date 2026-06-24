package com.tguimaraes.ledger.core.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class KafkaConfig {

    @Bean
    fun topic(): NewTopic {
        return TopicBuilder.name("ledger-events")
            .partitions(1)
            .replicas(1)
            .build()
    }
}
