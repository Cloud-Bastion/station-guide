package dev.aventix.station.resource.server.schedule

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.PagingAndSortingRepository
import java.time.Instant
import java.util.UUID

interface ScheduleEntryRepository: PagingAndSortingRepository<ScheduleEntry, UUID> {

    fun findByStartGreaterThanEqualAndEndLessThanEqual(
        start: Instant,
        end: Instant,
    ): List<ScheduleEntry>

}