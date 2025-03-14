package dev.aventix.station.resource.server.schedule

import dev.aventix.station.resource.server.employee.EmployeeCreateRequest
import dev.aventix.station.resource.server.employee.EmployeeService
import dev.aventix.station.resource.server.employee.address.EmployeeAddressDTO
import dev.aventix.station.resource.server.schedule.event.ScheduleEvent
import dev.aventix.station.resource.server.schedule.event.ScheduleEventCreateRequest
import dev.aventix.station.resource.server.schedule.event.ScheduleEventDto
import dev.aventix.station.resource.server.schedule.event.ScheduleEventType
import jakarta.annotation.PostConstruct
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.testcontainers.shaded.com.google.common.collect.Lists
import java.time.Instant
import java.time.LocalDate
import java.util.*

@Service
class ScheduleEntryService(
    private val repository: ScheduleEntryRepository,
    private val employeeService: EmployeeService,
) {

    @PostConstruct
    fun init() {

    }

    fun findAllPlannedEntriesBetween(start: Instant, end: Instant): List<ScheduleEntry> {
        val specification = Specification
            .where(ScheduleEntrySpecifications.isPlanned())
            .and(ScheduleEntrySpecifications.isWithinRange(start, end))
        return repository.findAll(specification)
    }

    fun create(request: ScheduleEntryCreateRequest) {
        val entity = ScheduleEntry().apply {
            this.planType = request.planType
            this.assignee = employeeService.findById(request.assigneeId)
                .orElseThrow { EntityNotFoundException("Cannot assign schedule entry to non-existing employee.") }
            this.events = request.initialEvents.map { event ->
                ScheduleEvent().apply {
                    this.eventType = event.eventType
                    this.timestamp = event.timestamp
                }
            }.toMutableList()
        }
        repository.saveAndFlush(entity)
    }

}