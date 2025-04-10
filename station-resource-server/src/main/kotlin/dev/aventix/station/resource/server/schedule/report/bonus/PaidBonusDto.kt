package dev.aventix.station.resource.server.schedule.report.bonus

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

data class PaidBonusDto(
    val id: UUID,
    val type: String,
    val percentage: Double,
    val addPerHour: Double,
    var appliesFrom: LocalTime?,
    var appliesTo: LocalTime?,
    var appliesOn: LocalDate?,
    var startingFrom: LocalDate?,
    var expiresOn: LocalDate?,
    var applyOnBankHolidays: Boolean,
    var applyOnWeekdays: List<Int>
) {

    fun appliesOn(date: LocalDate): Boolean {
        return appliesOn?.isEqual(date) == true
                || applyOnWeekdays.contains(date.dayOfWeek.value)
               // || (appliesFrom?.isBefore(date) || a)

    }

    fun appliesAt(timestamp: LocalDateTime): Boolean {
        return false
    }

}