package dev.aventix.station.resource.server.schedule.report.bonus

import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class PaidBonusDto(
    val id: UUID,
    val type: String,
    val percentage: Double,
    val addPerHour: Double,
    var appliedFrom: LocalTime?,
    var appliedTo: LocalTime?,
    var appliesOn: LocalDate?,
    var startingFrom: LocalDate?,
    var expiresOn: LocalDate?,
    var applyOnBankHolidays: Boolean,
    var applyOnWeekdays: List<Int>
) {
}