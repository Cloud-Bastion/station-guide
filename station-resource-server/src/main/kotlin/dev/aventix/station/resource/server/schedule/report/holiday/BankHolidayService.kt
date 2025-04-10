package dev.aventix.station.resource.server.schedule.report.holiday

import dev.aventix.station.resource.server.schedule.report.holiday.request.BankHoliday
import dev.aventix.station.resource.server.schedule.report.holiday.request.BankHolidayResponse
import dev.aventix.station.resource.server.schedule.report.holiday.request.FederalState
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.time.LocalDate

@Service
class BankHolidayService(
    private var bankHolidayRepository: BankHolidayRepository
) {
    @PostConstruct
    private fun bankHolidaysCheck() {
        if (this.bankHolidayRepository.count() == 0L || this.bankHolidayRepository.findAll().any { it.lastUpdate.monthValue != LocalDate.now().monthValue }) {
            println("Updating bank holidays, because its not up to date anymore")
            this.updateBankHolidays(LocalDate.now().year)
        }
    }

    fun isBankHoliday(date: LocalDate, state: FederalState): Boolean {
        return bankHolidayRepository.findByDateAndState(date, state) != null
    }

    private fun updateBankHolidays(year: Int): List<BankHolidayDto> {
        val restTemplate = RestTemplate()
        val response = restTemplate.getForObject<BankHolidayResponse>("https://get.api-feiertage.de/?years=$year")
        val entities: MutableList<BankHolidayEntity> = mutableListOf()

        response.bankHolidays.forEach {
            val checks = listOf(
                it.badenWurttemberg to FederalState.BADEN_WURTTEMBERG,
                it.bavaria to FederalState.BAVARIA,
                it.berlin to FederalState.BERLIN,
                it.brandenburg to FederalState.BRANDENBURG,
                it.bremen to FederalState.BREMEN,
                it.hamburg to FederalState.HAMBURG,
                it.hesse to FederalState.HESSE,
                it.mecklenburgVorpommern to FederalState.MECKLENBURG_VORPOMMERN,
                it.lowerSaxony to FederalState.LOWER_SAXONY,
                it.northRhineWestphalia to FederalState.NORTH_RHINE_WESTPHALIA,
                it.rhinelandPalatinate to FederalState.RHINELAND_PALATINATE,
                it.saarland to FederalState.SAARLAND,
                it.saxony to FederalState.SAXONY,
                it.saxonyAnhalt to FederalState.SAXONY_ANHALT,
                it.schleswigHolstein to FederalState.SCHLESWIG_HOLSTEIN,
                it.thuringia to FederalState.THURINGIA
            )

            checks.forEach { (value, state) ->
                if (value == 1) entities.add(getBase(it, state))
            }
        }

        this.bankHolidayRepository.deleteAll()
        bankHolidayRepository.saveAllAndFlush(entities)
        return entities.map { it.toDto() }
    }

    private fun getBase(bankHoliday: BankHoliday, state: FederalState): BankHolidayEntity {
        return BankHolidayEntity().apply {
            this.name = bankHoliday.name
            this.date = bankHoliday.date
            this.country = "GER"
            this.note = bankHoliday.comment
            this.state = state
            this.lastUpdate = LocalDate.now()
        }
    }

    fun getAll(): List<BankHolidayDto> {
        return this.bankHolidayRepository.findAll().map(BankHolidayEntity::toDto)
    }

}