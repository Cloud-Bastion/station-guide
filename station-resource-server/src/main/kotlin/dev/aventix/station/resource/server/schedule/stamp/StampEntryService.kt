package dev.aventix.station.resource.server.schedule.stamp

import dev.aventix.station.resource.server.employee.EmployeeEntity
import dev.aventix.station.resource.server.employee.EmployeeService
import jakarta.annotation.PostConstruct
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*

@Service
class StampEntryService(
    private val stampRepository: StampEntryRepository,
    private val employeeService: EmployeeService,
) {

    /**
     * To be executed when an employee checks in to work.
     *
     * It checks whether the last shift is longer than 11 hours ago.
     *
     */
    fun validateStartStamp() {

    }

    /**
     * To be executed when an employee checks out of work.
     *
     * It checks whether the next scheduled shift is at least
     * 11 hours in the future.
     */
    fun validateStopStamp() {

    }

    /**
     * Calculates the actual productive work time of a given employee
     * in the given time-frame. This method automatically deducts
     * break time.
     */
    @PostConstruct
    fun test() {
        var fakeStamps = listOf(StampEntryDto(
            UUID.randomUUID(),
            UUID.randomUUID(),
            ZonedDateTime.of(2025,3,29,22,0,0,0, ZoneId.of("Europe/Berlin")),
            StampEntryType.START,
        ), StampEntryDto(
            UUID.randomUUID(),
            UUID.randomUUID(),
            ZonedDateTime.of(2025,3,30,1,55,0,0, ZoneId.of("Europe/Berlin")),
            StampEntryType.BREAK_START,
        ), StampEntryDto(
            UUID.randomUUID(),
            UUID.randomUUID(),
            ZonedDateTime.of(2025,3,30,3,25,0,0, ZoneId.of("Europe/Berlin")),
            StampEntryType.BREAK_END,
        ), StampEntryDto(
            UUID.randomUUID(),
            UUID.randomUUID(),
            ZonedDateTime.of(2025,3,30,6,0,0,0, ZoneId.of("Europe/Berlin")),
            StampEntryType.END,
        ))

        val workingTime = calculateWorkTime(fakeStamps)
        println("worked hours: " + workingTime.toHours())
        println("worked minuted: " + (workingTime.toMinutes() - 60*workingTime.toHours()))
    }

    fun getStampsInRange(startIncluding: LocalDate, endIncluding: LocalDate, employee: EmployeeEntity): List<StampEntryDto> {
        return stampRepository.findAllInRange(startIncluding, endIncluding, employee).map { it.toDto() }
    }

    fun calculateWorkTime(stamps: List<StampEntryDto>): Duration {
        var workingTime: Duration = Duration.ZERO
        var previousStart: StampEntryDto? = null
        var previousBreakStart: StampEntryDto? = null

        StampEntryDto.sorted(stamps).forEachOrdered { stamp ->
            if (stamp.eventType == StampEntryType.START) {
                println("Setting previous event")
                previousStart = stamp
            } else if (stamp.eventType == StampEntryType.END) {
                if (previousStart != null && previousStart?.eventType == StampEntryType.START) {
                    println("Adding hours between " + previousStart?.timestamp + " and " + stamp.timestamp)
                    workingTime += Duration.between(previousStart?.timestamp, stamp.timestamp)
                }
            } else if (stamp.eventType == StampEntryType.BREAK_START) {
                previousBreakStart = stamp
            } else if (stamp.eventType == StampEntryType.BREAK_END) {
                if (previousBreakStart != null && previousBreakStart?.eventType == StampEntryType.BREAK_START) {
                    workingTime -= Duration.between(previousBreakStart?.timestamp, stamp.timestamp)
                }
            }
        }

        return workingTime
    }

    fun create(createRequest: StampEntryCreateRequest): StampEntryDto {
        val employee = employeeService.findById(createRequest.assigneeId).orElseThrow {
            EntityNotFoundException("Cannot assign stamp to non-existing employee ${createRequest.assigneeId}")
        }
        val entity = StampEntry().apply {
            this.assignee = employee
            this.eventType = createRequest.eventType
            this.timestamp = createRequest.timestamp.toOffsetDateTime()
        }
        stampRepository.saveAndFlush(entity)
        return entity.toDto()
    }

}