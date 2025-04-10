package dev.aventix.station.resource.server.schedule.report.holiday.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class BankHolidayResponse(
    @JsonProperty("feiertage")
    val bankHolidays: List<BankHoliday>,

    // success | error
    val status: String,
)