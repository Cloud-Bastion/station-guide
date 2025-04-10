package dev.aventix.station.resource.server.schedule.shift

import dev.aventix.station.resource.server.schedule.stamp.StampEntryDto
import dev.aventix.station.resource.server.schedule.stamp.StampEntryType
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

@Service
class ShiftTest {

    @PostConstruct
    fun init() {
        val stamps = listOf(


            StampEntryDto(
                UUID.randomUUID(),
                UUID.randomUUID(),
                ZonedDateTime.of(2025, 4, 1, 18, 0, 0, 0, ZoneId.of("Europe/Berlin")),
                StampEntryType.BREAK_START
            ),

            StampEntryDto(
                UUID.randomUUID(),
                UUID.randomUUID(),
                ZonedDateTime.of(2025, 4, 1, 18, 30, 0, 0, ZoneId.of("Europe/Berlin")),
                StampEntryType.BREAK_END
            ),

            StampEntryDto(
                UUID.randomUUID(),
                UUID.randomUUID(),
                ZonedDateTime.of(2025, 4, 1, 14, 0, 0, 0, ZoneId.of("Europe/Berlin")),
                StampEntryType.START
            ),

            StampEntryDto(
                UUID.randomUUID(),
                UUID.randomUUID(),
                ZonedDateTime.of(2025, 4, 1, 22, 0, 0, 0, ZoneId.of("Europe/Berlin")),
                StampEntryType.END
            ),

            StampEntryDto(
                UUID.randomUUID(),
                UUID.randomUUID(),
                ZonedDateTime.of(2025, 4, 2, 22, 0, 0, 0, ZoneId.of("Europe/Berlin")),
                StampEntryType.END
            ),

            StampEntryDto(
                UUID.randomUUID(),
                UUID.randomUUID(),
                ZonedDateTime.of(2025, 4, 2, 14, 30, 0, 0, ZoneId.of("Europe/Berlin")),
                StampEntryType.START
            ),


            StampEntryDto(
                UUID.randomUUID(),
                UUID.randomUUID(),
                ZonedDateTime.of(2025, 4, 2, 16, 30, 0, 0, ZoneId.of("Europe/Berlin")),
                StampEntryType.BREAK_END
            ),



                    StampEntryDto(
                    UUID.randomUUID(),
            UUID.randomUUID(),
            ZonedDateTime.of(2025, 4, 3, 14, 0, 0, 0, ZoneId.of("Europe/Berlin")),
            StampEntryType.START
        ),

            StampEntryDto(
                UUID.randomUUID(),
                UUID.randomUUID(),
                ZonedDateTime.of(2025, 4, 2, 16, 20, 0, 0, ZoneId.of("Europe/Berlin")),
                StampEntryType.BREAK_START
            ),

            )

        val shifts = Shift.fromStampSet(stamps)
        println(shifts)
    }

}