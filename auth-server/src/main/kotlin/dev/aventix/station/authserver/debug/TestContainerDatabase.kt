package dev.aventix.station.authserver.debug

import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.testcontainers.containers.PostgreSQLContainer

@Profile("test", "local")
@Configuration
class TestContainerDatabase {
    @Bean
    @ServiceConnection
    fun postgresContainer() =
        PostgreSQLContainer("postgres:17-alpine").withUsername("aventica").withPassword("aventica").apply {
            setPortBindings(listOf("5432:5432"))
        }.withReuse(false)
}