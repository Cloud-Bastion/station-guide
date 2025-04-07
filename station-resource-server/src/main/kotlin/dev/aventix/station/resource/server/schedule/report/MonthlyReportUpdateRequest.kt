package dev.aventix.station.resource.server.schedule.report

import java.util.UUID

data class MonthlyReportUpdateRequest(
    val employeeId: UUID,
    val year: Int,
    val month: Int,
)