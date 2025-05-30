package dev.aventix.station.resource.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import java.time.Duration
import java.time.OffsetDateTime

@SpringBootApplication
@EnableWebSecurity
@EnableScheduling
class StationResourceServerApplication

fun main(args: Array<String>) {
    runApplication<StationResourceServerApplication>(*args)

}