package dev.aventix.station.resource.server.schedule

import java.time.Instant
import java.util.UUID

data class ScheduleEntryCreateRequest(
    val assigneeId: UUID,
    val planType: ScheduleEntryPlanType,
    val entryType: ScheduleEntryType,
    val startDate: Instant,
    val endDate: Instant,
)