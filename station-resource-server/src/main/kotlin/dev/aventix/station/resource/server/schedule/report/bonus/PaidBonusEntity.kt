package dev.aventix.station.resource.server.schedule.report.bonus

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

@Entity
@Table(name = "employee_pay")
class PaidBonusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    lateinit var id: UUID

    lateinit var type: String

    var percentage: Double = 0.0

    var addPerHour: Double = 0.0

    var appliesFrom: LocalTime? = null

    var appliesTo: LocalTime? = null

    var appliesOn: LocalDate? = null

    var startingFrom: LocalDate? = null

    var expiresOn: LocalDate? = null

    var applyOnHoliday: Boolean = false

    @ElementCollection
    var applyOnWeekDays: MutableList<Int> = mutableListOf()

    fun toDto(): PaidBonusDto {
        return PaidBonusDto(id, type, percentage, addPerHour, appliesFrom, appliesTo, appliesOn, startingFrom, expiresOn, applyOnHoliday, applyOnWeekDays)
    }

}