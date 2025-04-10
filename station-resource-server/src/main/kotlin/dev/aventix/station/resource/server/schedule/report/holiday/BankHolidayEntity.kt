package dev.aventix.station.resource.server.schedule.report.holiday

import dev.aventix.station.resource.server.schedule.report.holiday.request.FederalState
import jakarta.persistence.*
import java.time.LocalDate
import java.util.*

@Entity
@Table(name = "bank_holidays")
class BankHolidayEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    lateinit var id: UUID

    @Temporal(TemporalType.DATE)
    lateinit var date: LocalDate

    lateinit var country: String

    @Enumerated(EnumType.STRING)
    lateinit var state: FederalState

    lateinit var name: String

    lateinit var note: String

    @Temporal(TemporalType.DATE)
    lateinit var lastUpdate: LocalDate

    fun toDto(): BankHolidayDto {
        return BankHolidayDto(id, date, country, state, name, note)
    }
}