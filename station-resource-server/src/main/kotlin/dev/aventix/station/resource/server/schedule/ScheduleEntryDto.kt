package dev.aventix.station.resource.server.schedule

import dev.aventix.station.resource.server.employee.EmployeeDTO
import java.time.Instant
import java.util.UUID

data class ScheduleEntryDto(
    val id: UUID,
    val assignee: EmployeeDTO,
    val planType: ScheduleEntryPlanType,
    val entryType: ScheduleEntryType,
    val startDate: Instant,
    val endDate: Instant,
)