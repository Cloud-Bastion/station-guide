package dev.aventix.station.resource.server.schedule.report.holiday

import dev.aventix.station.resource.server.schedule.report.holiday.request.FederalState
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
interface BankHolidayRepository: JpaRepository<BankHolidayEntity, UUID> {
    fun findByDateAndState(date: LocalDate, state: FederalState): BankHolidayEntity?
}