package dev.aventix.station.resource.server.schedule.event

import java.time.Instant
import java.util.UUID

data class ScheduleEventDto(
    val id: UUID,
    val timestamp: Instant,
    val eventType: ScheduleEventType,
)