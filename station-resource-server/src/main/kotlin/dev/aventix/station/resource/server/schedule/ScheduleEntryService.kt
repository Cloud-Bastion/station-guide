package dev.aventix.station.resource.server.schedule

import dev.aventix.station.resource.server.employee.EmployeeCreateRequest
import dev.aventix.station.resource.server.employee.EmployeeService
import dev.aventix.station.resource.server.employee.address.EmployeeAddressDTO
import dev.aventix.station.resource.server.schedule.stamp.StampEntry
import dev.aventix.station.resource.server.schedule.stamp.StampEntryCreateRequest
import dev.aventix.station.resource.server.schedule.stamp.StampEntryType
import jakarta.annotation.PostConstruct
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class ScheduleEntryService(
    private val repository: PlannedScheduleEntryRepository,
    private val employeeService: EmployeeService,
) {

    @PostConstruct
    fun init() {
        val employee = employeeService.create(EmployeeCreateRequest(
            "Test",
            "TestL",
            "test-mail",
            EmployeeAddressDTO(55131, "Testhausen", "Teststraße", "12a", null),
            LocalDate.now(),
            273843L,
            "E016",
            null,
            LocalDateTime.now()
        ))
    }

    // 11 h break between 6-hour shifts
    // below 6 h per day -> no recreation
    fun breaksViolated(employeeId: UUID) {
        // 03.04.: 22:00
        // 04.04.: 06:00
        // ------
        // 04.04.: 22:00
        // 05.04.: 06.00

    }
//
//    fun findAllPlannedEntriesBetween(start: Instant, end: Instant): List<PlannedScheduleEntry> {
//        val specification = Specification
//            .where(ScheduleEntrySpecifications.isPlanned())
//            .and(ScheduleEntrySpecifications.isWithinRange(start, end))
//        return repository.findAll(specification)
//    }

    fun create(request: PlannedScheduleEntryCreateRequest) {
        val entity = PlannedScheduleEntry().apply {
            this.entryType = request.entryType
            this.assignee = employeeService.findById(request.assigneeId)
                .orElseThrow { EntityNotFoundException("Cannot assign schedule entry to non-existing employee.") }
            this.breakMinutes = breakMinutes
            this.startTime = request.startTime
            this.endTime = request.endTime
        }
        repository.saveAndFlush(entity)
    }

}