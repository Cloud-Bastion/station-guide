package dev.aventix.station.resource.server.schedule

import java.time.Instant
import java.util.*

data class ScheduleEntryPatchRequest(
    val id: UUID,
    val planType: ScheduleEntryPlanType,
    val entryType: ScheduleEntryType,
    val startDate: Instant,
    val endDate: Instant,
)