package dev.aventix.station.resource.server.schedule

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import java.time.Instant
import java.util.UUID

interface ScheduleEntryRepository:
    JpaRepository<ScheduleEntry, UUID>,
    PagingAndSortingRepository<ScheduleEntry, UUID>,
    JpaSpecificationExecutor<ScheduleEntry> {

}