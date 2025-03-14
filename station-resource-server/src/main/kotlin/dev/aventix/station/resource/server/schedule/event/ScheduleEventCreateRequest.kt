package dev.aventix.station.resource.server.schedule.event

import java.time.Instant

data class ScheduleEventCreateRequest(
    val timestamp: Instant,
    val eventType: ScheduleEventType,
)