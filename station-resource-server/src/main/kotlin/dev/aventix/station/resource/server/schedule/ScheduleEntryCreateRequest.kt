package dev.aventix.station.resource.server.schedule

import dev.aventix.station.resource.server.schedule.event.ScheduleEvent
import dev.aventix.station.resource.server.schedule.event.ScheduleEventCreateRequest
import dev.aventix.station.resource.server.schedule.event.ScheduleEventDto
import java.util.UUID

data class ScheduleEntryCreateRequest(
    val assigneeId: UUID,
    val planType: ScheduleEntryPlanType,
    val entryType: ScheduleEntryType,
    val initialEvents: List<ScheduleEventCreateRequest>,
)