package dev.aventix.station.resource.server.schedule.report.holiday.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class BankHoliday(
    val date: LocalDate,

    @JsonProperty("fname")
    val name: String,

    val comment: String,

    @JsonProperty("bw")
    val badenWurttemberg: Int,

    @JsonProperty("by")
    val bavaria: Int,

    @JsonProperty("be")
    val berlin: Int,

    @JsonProperty("bb")
    val brandenburg: Int,

    @JsonProperty("hb")
    val bremen: Int,

    @JsonProperty("hh")
    val hamburg: Int,

    @JsonProperty("he")
    val hesse: Int,

    @JsonProperty("mv")
    val mecklenburgVorpommern: Int,

    @JsonProperty("ni")
    val lowerSaxony: Int,

    @JsonProperty("nw")
    val northRhineWestphalia: Int,

    @JsonProperty("rp")
    val rhinelandPalatinate: Int,

    @JsonProperty("sl")
    val saarland: Int,

    @JsonProperty("sn")
    val saxony: Int,

    @JsonProperty("st")
    val saxonyAnhalt: Int,

    @JsonProperty("sh")
    val schleswigHolstein: Int,

    @JsonProperty("th")
    val thuringia: Int,
)