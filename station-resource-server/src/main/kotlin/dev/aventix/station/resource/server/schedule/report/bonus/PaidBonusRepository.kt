package dev.aventix.station.resource.server.schedule.report.bonus

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Repository
interface PaidBonusRepository: JpaRepository<PaidBonusEntity, UUID> {
    fun findAllByStartingFromGreaterThanAndExpiresOnLessThan(
        startingFrom: LocalDate,
        expiresOn: LocalDate
    ): List<PaidBonusEntity>
}