package dev.aventix.station.resource.server.schedule.stamp

import java.time.ZonedDateTime
import java.util.UUID

data class StampEntryCreateRequest(
    val assigneeId: UUID,
    val timestamp: ZonedDateTime,
    val eventType: StampEntryType,
)