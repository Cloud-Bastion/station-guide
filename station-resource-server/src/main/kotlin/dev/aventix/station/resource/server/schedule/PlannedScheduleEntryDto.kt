package dev.aventix.station.resource.server.schedule

import dev.aventix.station.resource.server.employee.EmployeeDTO
import java.time.Instant
import java.util.UUID

data class PlannedScheduleEntryDto(
    val id: UUID,
    val assignee: EmployeeDTO,
    val entryType: PlannedScheduleEntryType,
    val startTime: Instant,
    val endTime: Instant,
    val breakMinutes: Int,
)