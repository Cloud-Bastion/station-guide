package dev.aventix.station.resource.server.database

import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.testcontainers.containers.PostgreSQLContainer

@Profile("test", "local")
@Configuration
class DatabaseDevConfiguration {
    @Bean
    @ServiceConnection
    fun postgresContainer() =
        PostgreSQLContainer("postgres:17-alpine").withUsername("aventica").withPassword("aventica").apply {
            setPortBindings(listOf("5432:5432"))
        }.withReuse(false)
}