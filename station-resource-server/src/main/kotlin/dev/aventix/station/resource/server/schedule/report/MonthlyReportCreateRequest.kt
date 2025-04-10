package dev.aventix.station.resource.server.schedule.report

data class MonthlyReportCreateRequest(
    val year: Int,
    val month: Int,
)