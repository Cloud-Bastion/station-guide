package dev.aventix.station.resource.server.schedule.report.bonus

import dev.aventix.station.resource.server.schedule.report.holiday.BankHolidayService
import dev.aventix.station.resource.server.schedule.stamp.StampEntryDto
import dev.aventix.station.resource.server.schedule.stamp.StampEntryType
import org.springframework.stereotype.Service

@Service
class PaidBonusService(
    private val paidBonusRepository: PaidBonusRepository,
    private val bankHolidayService: BankHolidayService,
) {

    fun getBonusHours(stamps: Collection<StampEntryDto>): Map<PaidBonusDto, Double> {
        val bonusMap = mutableMapOf<PaidBonusDto, Double>()
        val sortedStamps = StampEntryDto.sorted(stamps)
        val bonuses = paidBonusRepository
            .findAllByStartingFromGreaterThanAndExpiresOnLessThan(
                sortedStamps.toList().last().timestamp.toLocalDate(),
                sortedStamps.toList().first().timestamp.toLocalDate()
            ).map { it.toDto() }

        var previousStamp: StampEntryDto? = null
        sortedStamps.forEachOrdered {
            bonuses.forEach { bonus ->
                /*
                    - BREAK START:
                 */


                if (it.timestamp.toLocalDate().isEqual(bonus.appliesOn)) {
                    // bonus applied on the current date

                    // if (it.eventType == StampEntryType.BREAK_END)

                        bonusMap[bonus] = 0.0
                    //Check shift and work hours and deploy to map
                } else if (bonus.applyOnWeekdays.contains(it.timestamp.dayOfWeek.value)) {
                    // applies on the current day of week
                }
            }
        }

        return mapOf()
    }

}