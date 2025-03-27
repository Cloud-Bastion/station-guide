package dev.aventix.station.resource.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StationResourceServerApplication

fun main(args: Array<String>) {
    runApplication<StationResourceServerApplication>(*args)
}
