package dev.aventix.station.resource.server.schedule.report.holiday

import dev.aventix.station.resource.server.schedule.report.holiday.request.FederalState
import java.time.LocalDate
import java.util.UUID

data class BankHolidayDto(
    val id: UUID,
    val date: LocalDate,
    val country: String,
    val state: FederalState,
    val name: String,
    val note: String,
)