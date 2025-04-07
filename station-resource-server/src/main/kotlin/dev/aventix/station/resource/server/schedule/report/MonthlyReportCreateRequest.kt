package dev.aventix.station.resource.server.schedule.report

import java.util.UUID

data class MonthlyReportCreateRequest(
    val employeeId: UUID,
    val year: Int,
    val month: Int,
)