package dev.aventix.station.resource.server.schedule

import java.time.Instant
import java.util.*

data class PlannedScheduleEntryPatchRequest(
    val id: UUID,
    val entryType: PlannedScheduleEntryType,
    val startDate: Instant,
    val endDate: Instant,
    val breakMinutes: Int,
)