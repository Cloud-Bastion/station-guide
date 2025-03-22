package dev.aventix.station.resource.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

@SpringBootApplication
@EnableWebSecurity
class StationResourceServerApplication

fun main(args: Array<String>) {
    runApplication<StationResourceServerApplication>(*args)
}
