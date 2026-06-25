package com.tguimaraes.ledger.core.support

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.kafka.KafkaContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    companion object {
        private val kafkaInstance = KafkaContainer(DockerImageName.parse("apache/kafka:4.0.0"))
        private val postgresInstance = PostgreSQLContainer(DockerImageName.parse("postgres:16"))
        // private val redisInstance = GenericContainer(DockerImageName.parse("redis:7-alpine")).withExposedPorts(6379)

        @Bean
        @ServiceConnection
        @JvmStatic
        fun kafkaContainer(): KafkaContainer = kafkaInstance

        @Bean
        @ServiceConnection
        @JvmStatic
        fun postgresContainer(): PostgreSQLContainer<*> = postgresInstance


        // @Bean
        // @ServiceConnection(name = "redis")
        // @JvmStatic
        // fun redisContainer(): GenericContainer<*> = redisInstance
    }
}