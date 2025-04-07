package dev.aventix.station.resource.server.schedule.report.bonus

import dev.aventix.station.resource.server.schedule.stamp.StampEntryDto
import dev.aventix.station.resource.server.schedule.stamp.StampEntryType
import org.springframework.stereotype.Service

@Service
class PaidBonusService(
    private val paidBonusRepository: PaidBonusRepository,
) {

    fun getBonusHours(stamps: Collection<StampEntryDto>): Map<PaidBonusDto, Double> {
        val bonusMap = mutableMapOf<PaidBonusDto, Double>()
        val sortedStamps = StampEntryDto.sorted(stamps)
        val bonuses = paidBonusRepository
            .findAllByStartingFromGreaterThanAndExpiresOnLessThan(
                sortedStamps.toList().last().timestamp.toLocalDate(),
                sortedStamps.toList().first().timestamp.toLocalDate()
            ).map { it.toDto() }

        sortedStamps.forEachOrdered {
            bonuses.forEach { bonus ->
                if (it.timestamp.toLocalDate().isEqual(bonus.appliesOn)) {
                    // if (it.eventType == StampEntryType.BREAK_END)

                        bonusMap[bonus] = 0.0
                    //Check shift and work hours and deploy to map
                }
            }
        }

        return mapOf()
    }

}