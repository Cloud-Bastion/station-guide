package dev.aventix.station.resource.server.schedule

import dev.aventix.station.resource.server.schedule.stamp.StampEntryCreateRequest
import java.time.LocalDateTime
import java.util.UUID

data class PlannedScheduleEntryCreateRequest(
    val assigneeId: UUID,
    val entryType: PlannedScheduleEntryType,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val breakMinutes: Int,
)