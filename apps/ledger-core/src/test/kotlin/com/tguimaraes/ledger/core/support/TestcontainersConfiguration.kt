package com.tguimaraes.ledger.core.support

import io.debezium.testing.testcontainers.DebeziumContainer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.Network
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.kafka.ConfluentKafkaContainer
import org.testcontainers.lifecycle.Startables
import org.testcontainers.utility.DockerImageName
import java.util.stream.Stream

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    companion object {
        private const val POSTGRES_DB_NAME = "testdb"
        private const val POSTGRES_USER = "postgres"
        private const val POSTGRES_PASSWORD = "postgres"

        val network: Network = Network.newNetwork()

        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:16")
            .withDatabaseName(POSTGRES_DB_NAME)
            .withUsername(POSTGRES_USER)
            .withPassword(POSTGRES_PASSWORD)
            .withNetwork(network)
            .withNetworkAliases("postgres")
            .withCommand("postgres", "-c", "wal_level=logical")

        val kafka: ConfluentKafkaContainer = ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"))
            .withNetwork(network)
            .withNetworkAliases("kafka")

        val debezium: DebeziumContainer = DebeziumContainer("quay.io/debezium/connect:3.5.2.Final")
            .withNetwork(network)
            .withKafka(network, "kafka:9093")
            .dependsOn(postgres, kafka)
            .withEnv("GROUP_ID", "1")
            .withEnv("CONFIG_STORAGE_TOPIC", "my_connect_configs")
            .withEnv("OFFSET_STORAGE_TOPIC", "my_connect_offsets")
            .withEnv("STATUS_STORAGE_TOPIC", "my_connect_statuses")

        init {
            Startables.deepStart(
                Stream.of(postgres, kafka, debezium)
            ).join()

            DebeziumConnectorRegistrar.register(
                debezium,
                POSTGRES_DB_NAME,
                POSTGRES_USER,
                POSTGRES_PASSWORD
            )
        }

        @Bean
        @ServiceConnection
        fun postgresContainer(): PostgreSQLContainer<*> = postgres

        @Bean
        @ServiceConnection
        fun kafkaContainer(): ConfluentKafkaContainer = kafka

        @Bean
        fun debeziumContainer(): DebeziumContainer = debezium
    }
}
